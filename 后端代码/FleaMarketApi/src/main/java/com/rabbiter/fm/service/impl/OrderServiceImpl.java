package com.rabbiter.fm.service.impl;

import com.rabbiter.fm.service.OrderService;
import com.rabbiter.fm.service.UserService;
import com.rabbiter.fm.dao.IdleItemDao;
import com.rabbiter.fm.dao.OrderDao;
import com.rabbiter.fm.dao.UserDao;
import com.rabbiter.fm.model.IdleItemModel;
import com.rabbiter.fm.model.OrderModel;
import com.rabbiter.fm.model.UserModel;
import com.rabbiter.fm.vo.PageVo;
import com.rabbiter.fm.common.utils.OrderTask;
import com.rabbiter.fm.common.utils.OrderTaskHandler;
import com.rabbiter.fm.utils.ServiceFeeCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Resource
    private IdleItemDao idleItemDao;
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    /**
     * 新增订单，同时减少库存
     * 用了事务串行化，后续要优化，修改更新的sql，增加更新条件，而不是在代码中判断条件
     * 业务逻辑可优化，改为支付时才下架。
     * 新功能待做，需要新增订单超时处理
     * （订单超时：
     * 1、恢复闲置库存；2、修改订单状态；
     * 3、确保订单取消前不会影响用户的支付，支付前要判断订单状态并加读锁，取消订单时要判断订单状态为未支付才能取消；
     * 4、保证延期任务一定执行，即确保任务不会因为系统异常而消失）
     * @param orderModel
     * @return
     */

    private static HashMap<Integer,ReentrantLock> lockMap=new HashMap<>();
    static {
//        ReentrantLock lock=new ReentrantLock(true);
        for(int i=0;i<100;i++){
            lockMap.put(i,new ReentrantLock(true));
        }
    }
    
    public boolean addOrder(OrderModel orderModel){
        IdleItemModel idleItemModel=idleItemDao.selectByPrimaryKey(orderModel.getIdleId());
        
        // 检查商品状态是否正常(已发布)
        if(idleItemModel.getIdleStatus()!=1){
            return false;
        }
        
        // 检查库存是否足够
        Integer quantity = orderModel.getPurchaseQuantity() != null ? orderModel.getPurchaseQuantity() : 1;
        if(idleItemModel.getIdleStock() < quantity){
            return false;
        }
        
        // 设置默认购买数量为1
        if(orderModel.getPurchaseQuantity() == null) {
            orderModel.setPurchaseQuantity(1);
        }
        
        // 只有在订单价格为空或者为0时才计算订单总价
        if (orderModel.getOrderPrice() == null || orderModel.getOrderPrice().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal totalPrice = idleItemModel.getIdlePrice().multiply(new BigDecimal(orderModel.getPurchaseQuantity()));
            orderModel.setOrderPrice(totalPrice);
        }
        
        // 商品库存小于等于购买数量时，将商品标记为下架
        boolean needToDown = idleItemModel.getIdleStock() <= quantity;
        
        IdleItemModel idleItem = new IdleItemModel();
        idleItem.setId(orderModel.getIdleId());
        idleItem.setUserId(idleItemModel.getUserId());
        
        // 只有当库存不足时才下架商品
        if(needToDown) {
            idleItem.setIdleStatus((byte)2);
        }

        int key = (int) (orderModel.getIdleId()%100);
        ReentrantLock lock = lockMap.get(key);
        boolean flag;
        try {
            lock.lock();
            flag = addOrderHelp(idleItem, orderModel, quantity, needToDown);
        } finally {
            lock.unlock();
        }
        return flag;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean addOrderHelp(IdleItemModel idleItem, OrderModel orderModel, Integer quantity, boolean needToDown){
        IdleItemModel idleItemModel = idleItemDao.selectByPrimaryKey(orderModel.getIdleId());
        
        // 再次检查商品状态是否正常
        if(idleItemModel.getIdleStatus()!=1){
            return false;
        }
        
        // 再次检查库存是否足够
        if(idleItemModel.getIdleStock() < quantity){
            return false;
        }
        
        // 减少商品库存
        int result = idleItemDao.decreaseStock(orderModel.getIdleId(), quantity);
        if(result != 1) {
            return false;
        }
        
        // 如果需要下架商品（库存为0），更新商品状态
        if(needToDown && idleItem.getIdleStatus() != null) {
            idleItemDao.updateByPrimaryKeySelective(idleItem);
        }
        
        // 设置订单的默认值，确保新增字段有值
        if(orderModel.getPointsUsed() == null) {
            orderModel.setPointsUsed(0);
        }
        if(orderModel.getPointsDiscount() == null) {
            orderModel.setPointsDiscount(BigDecimal.ZERO);
        }
        if(orderModel.getServiceFee() == null) {
            orderModel.setServiceFee(BigDecimal.ZERO);
        }
        if(orderModel.getServiceFeeRate() == null) {
            orderModel.setServiceFeeRate(BigDecimal.ZERO);
        }
        if(orderModel.getSellerAmount() == null) {
            orderModel.setSellerAmount(BigDecimal.ZERO);
        }
        
        // 创建订单 - 使用insertSelective方法
        if(orderDao.insertSelective(orderModel) == 1) {
            orderModel.setOrderStatus((byte) 4);
            //半小时未支付则取消订单
            OrderTaskHandler.addOrder(new OrderTask(orderModel, 30*60));
            return true;
        } else {
            throw new RuntimeException("创建订单失败");
        }
    }

    /**
     * 获取订单信息，同时获取对应的闲置信息
     * @param id
     * @return
     */
    public OrderModel getOrder(Long id){
        OrderModel orderModel=orderDao.selectByPrimaryKey(id);
        orderModel.setIdleItem(idleItemDao.selectByPrimaryKey(orderModel.getIdleId()));
        return orderModel;
    }

    /**
     * 更新订单状态，无验证，后期修改为定制的更新sql
     * 后期改为在支付时下架闲置
     * @param orderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(OrderModel orderModel){
        //不可修改的信息
        orderModel.setOrderNumber(null);
        orderModel.setUserId(null);
        orderModel.setIdleId(null);
        orderModel.setCreateTime(null);
        if(orderModel.getOrderStatus()==4){
            //取消订单,需要优化，减少数据库查询次数
            OrderModel o=orderDao.selectByPrimaryKey(orderModel.getId());
            if(o.getOrderStatus()!=0){
                return false;
            }
            
            // 取消订单时，需要恢复商品库存
            IdleItemModel idleItemModel = idleItemDao.selectByPrimaryKey(o.getIdleId());
            if(idleItemModel != null){
                IdleItemModel idleItem = new IdleItemModel();
                idleItem.setId(o.getIdleId());
                idleItem.setUserId(idleItemModel.getUserId());
                
                // 恢复库存
                Integer quantity = o.getPurchaseQuantity() != null ? o.getPurchaseQuantity() : 1;
                Integer newStock = idleItemModel.getIdleStock() + quantity;
                idleItem.setIdleStock(newStock);
                
                // 如果商品当前是下架状态，且不是因为用户主动下架，则重新上架
                if(idleItemModel.getIdleStatus() == 2) {
                    idleItem.setIdleStatus((byte)1);
                }
                
                // 更新订单状态为已取消
                if(orderDao.updateByPrimaryKeySelective(orderModel) == 1) {
                    // 更新商品库存和状态
                    if(idleItemDao.updateByPrimaryKeySelective(idleItem) == 1){
                        return true;
                    } else {
                        throw new RuntimeException("恢复商品库存失败");
                    }
                }
                return false;
            } else {
                // 商品不存在，但仍可以取消订单
                if(orderDao.updateByPrimaryKeySelective(orderModel) == 1){
                    return true;
                } else {
                    throw new RuntimeException("取消订单失败");
                }
            }
        }
        return orderDao.updateByPrimaryKeySelective(orderModel)==1;
    }

    /**
     * 获取我的所有订单
     * 同时查询出对应的闲置信息，
     * 未做分页
     * userId建索引
     * @param userId
     * @return
     */
    public List<OrderModel> getMyOrder(Long userId){
        List<OrderModel> list=orderDao.getMyOrder(userId);
        if(list.size()>0){
            List<Long> idleIdList=new ArrayList<>();
            for(OrderModel i:list){
                idleIdList.add(i.getIdleId());
            }
            List<IdleItemModel> idleItemModelList=idleItemDao.findIdleByList(idleIdList);
            Map<Long,IdleItemModel> map=new HashMap<>();
            for(IdleItemModel idle:idleItemModelList){
                map.put(idle.getId(),idle);
            }
            for(OrderModel i:list){
                i.setIdleItem(map.get(i.getIdleId()));
            }
        }
        return list;
    }

    /**
     * 查询用户卖出的闲置
     * @param userId
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<OrderModel> getMySoldIdle(Long userId){
        List<IdleItemModel> list=idleItemDao.getAllIdleItem(userId);
        List<OrderModel> orderList=null;
        if(list.size()>0){
            List<Long> idleIdList=new ArrayList<>();
            for(IdleItemModel i:list){
                idleIdList.add(i.getId());
            }
            orderList=orderDao.findOrderByIdleIdList(idleIdList);
            Map<Long,IdleItemModel> map=new HashMap<>();
            for(IdleItemModel idle:list){
                map.put(idle.getId(),idle);
            }
            for(OrderModel o:orderList){
                o.setIdleItem(map.get(o.getIdleId()));
            }
        }
        return orderList;
    }

    public PageVo<OrderModel> getAllOrder(int page, int nums){
        List<OrderModel> list=orderDao.getAllOrder((page-1)*nums,nums);
        if(list.size()>0){
            List<Long> idleIdList=new ArrayList<>();
            for(OrderModel i:list){
                idleIdList.add(i.getIdleId());
            }
            List<IdleItemModel> idleItemModelList=idleItemDao.findIdleByList(idleIdList);
            Map<Long,IdleItemModel> map=new HashMap<>();
            for(IdleItemModel idle:idleItemModelList){
                map.put(idle.getId(),idle);
            }
            for(OrderModel i:list){
                i.setIdleItem(map.get(i.getIdleId()));
            }
        }
        int count=orderDao.countAllOrder();
        return new PageVo<>(list,count);
    }

    public boolean deleteOrder(long id){
        return orderDao.deleteByPrimaryKey(id)==1;
    }
    
    /**
     * 使用账户余额支付订单
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否支付成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payWithBalance(Long orderId, Long userId) {
        // 获取订单信息
        OrderModel order = orderDao.selectByPrimaryKey(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 0 || order.getPaymentStatus() != 0) {
            return false;
        }
        
        // 扣减买家余额
        BigDecimal orderAmount = order.getOrderPrice().negate(); // 负数表示扣款
        boolean deductResult = userService.updateUserBalance(userId, orderAmount);
        if (!deductResult) {
            return false;
        }
        
        // 获取卖家ID并增加卖家余额
        IdleItemModel idleItem = idleItemDao.selectByPrimaryKey(order.getIdleId());
        if (idleItem == null) {
            return false;
        }
        
        Long sellerId = idleItem.getUserId();
        // 增加卖家余额（使用正数金额）
        boolean addResult = userService.updateUserBalance(sellerId, order.getOrderPrice());
        if (!addResult) {
            // 如果增加卖家余额失败，由于@Transactional注解，会自动回滚买家的扣款操作
            return false;
        }
        
        // 更新订单状态
        OrderModel updateOrder = new OrderModel();
        updateOrder.setId(orderId);
        updateOrder.setOrderStatus((byte) 1); // 设置为待发货
        updateOrder.setPaymentStatus((byte) 1); // 设置为已支付
        updateOrder.setPaymentWay("账户余额");
        updateOrder.setPaymentTime(new Date());
        
        boolean updateResult = orderDao.updateByPrimaryKeySelective(updateOrder) == 1;
        
        // 如果支付成功，增加商品销量
        if (updateResult) {
            // 获取购买数量
            Integer purchaseQuantity = order.getPurchaseQuantity() != null ? order.getPurchaseQuantity() : 1;
            // 增加商品销量
            idleItemDao.increaseSoldCount(order.getIdleId(), purchaseQuantity);
            
            // 为用户增加积分（积分 = 订单金额的整数部分）
            Integer pointsToAdd = order.getOrderPrice().intValue();
            if (pointsToAdd > 0) {
                // 获取用户当前积分，用于记录日志
                Integer currentPoints = userService.getUserPoints(userId);
                System.out.println("余额支付准备添加积分: userId=" + userId + ", 当前积分=" + currentPoints + ", 将添加积分=" + pointsToAdd);
                
                boolean addPointsResult = userService.updateUserPoints(userId, pointsToAdd);
                if (!addPointsResult) {
                    // 积分添加失败记录日志，但不影响支付结果
                    System.err.println("用户 " + userId + " 的积分添加失败，订单ID: " + orderId);
                } else {
                    // 再次获取用户积分，验证是否成功累加
                    Integer newPoints = userService.getUserPoints(userId);
                    System.out.println("余额支付积分添加完成: userId=" + userId + ", 添加前积分=" + currentPoints + 
                                     ", 添加积分=" + pointsToAdd + ", 添加后积分=" + newPoints);
                }
            }
        }
        
        return updateResult;
    }
    
    /**
     * 使用账户余额支付订单（带积分抵扣）
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param usePoints 使用的积分数量
     * @return 是否支付成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payWithBalanceAndPoints(Long orderId, Long userId, Integer usePoints) {
        // 获取订单信息
        OrderModel order = orderDao.selectByPrimaryKey(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 0 || order.getPaymentStatus() != 0) {
            return false;
        }
        
        // 检查用户积分是否足够
        Integer userPoints = userService.getUserPoints(userId);
        if (usePoints > userPoints) {
            return false;
        }
        
        // 计算积分抵扣的金额
        BigDecimal pointsDeduction = userService.calculatePointsDeduction(usePoints);
        
        // 确保抵扣金额不超过订单总额
        if (pointsDeduction.compareTo(order.getOrderPrice()) > 0) {
            pointsDeduction = order.getOrderPrice();
        }
        
        // 计算实际需要支付的金额
        BigDecimal actualPayment = order.getOrderPrice().subtract(pointsDeduction);
        
        // 扣减买家余额
        BigDecimal orderAmount = actualPayment.negate(); // 负数表示扣款
        boolean deductResult = userService.updateUserBalance(userId, orderAmount);
        if (!deductResult) {
            return false;
        }
        
        // 获取卖家ID并增加卖家余额
        IdleItemModel idleItem = idleItemDao.selectByPrimaryKey(order.getIdleId());
        if (idleItem == null) {
            return false;
        }
        
        Long sellerId = idleItem.getUserId();
        // 增加卖家余额（使用正数金额）- 卖家获得的是订单原价
        boolean addResult = userService.updateUserBalance(sellerId, order.getOrderPrice());
        if (!addResult) {
            // 如果增加卖家余额失败，由于@Transactional注解，会自动回滚买家的扣款操作
            return false;
        }
        
        // 更新订单状态
        OrderModel updateOrder = new OrderModel();
        updateOrder.setId(orderId);
        updateOrder.setOrderStatus((byte) 1); // 设置为待发货
        updateOrder.setPaymentStatus((byte) 1); // 设置为已支付
        updateOrder.setPaymentWay("账户余额+积分");
        updateOrder.setPaymentTime(new Date());
        // 保存使用的积分数量和抵扣金额
        updateOrder.setPointsUsed(usePoints);
        updateOrder.setPointsDiscount(pointsDeduction);
        
        boolean updateResult = orderDao.updateByPrimaryKeySelective(updateOrder) == 1;
        
        if (updateResult) {
            // 获取购买数量并增加商品销量
            Integer purchaseQuantity = order.getPurchaseQuantity() != null ? order.getPurchaseQuantity() : 1;
            idleItemDao.increaseSoldCount(order.getIdleId(), purchaseQuantity);
            
            // 扣减用户积分
            System.out.println("准备扣减积分: userId=" + userId + ", usePoints=" + usePoints + ", 当前积分=" + userPoints);
            try {
                boolean deductPointsResult = userService.deductUserPoints(userId, usePoints, userPoints);
                
                if (!deductPointsResult) {
                    // 积分扣减失败，记录错误原因
                    System.err.println("积分扣减失败: userId=" + userId + ", usePoints=" + usePoints + ", 当前积分=" + userPoints);
                    throw new RuntimeException("积分扣减失败");
                }
                
                // 为用户增加新的积分（积分 = 实际支付金额的整数部分）
                Integer pointsToAdd = actualPayment.intValue();
                if (pointsToAdd > 0) {
                    System.out.println("准备添加积分: userId=" + userId + ", pointsToAdd=" + pointsToAdd);
                    boolean addPointsResult = userService.updateUserPoints(userId, pointsToAdd);
                    if (!addPointsResult) {
                        // 积分添加失败记录日志，但不影响支付结果
                        System.err.println("用户 " + userId + " 的积分添加失败，订单ID: " + orderId);
                    }
                }
            } catch (Exception e) {
                System.err.println("积分操作异常: " + e.getMessage());
                throw e; // 重新抛出异常以触发事务回滚
            }
        }
        
        return updateResult;
    }

    // 新增：服务费计算相关方法实现
    /**
     * 计算并设置订单的服务费信息
     * @param orderModel 订单模型
     * @param sellerId 卖家ID
     */
    @Override
    public void calculateAndSetServiceFee(OrderModel orderModel, Long sellerId) {
        // 获取卖家等级
        Byte sellerLevel = userService.getUserLevel(sellerId);
        
        // 使用服务费计算器计算服务费信息
        ServiceFeeCalculator.ServiceFeeInfo feeInfo = 
            ServiceFeeCalculator.calculateAllFees(orderModel.getOrderPrice(), sellerLevel);
        
        // 设置订单的服务费信息
        orderModel.setServiceFeeRate(feeInfo.getRate());
        orderModel.setServiceFee(feeInfo.getServiceFee());
        orderModel.setSellerAmount(feeInfo.getSellerAmount());
        
        System.out.println("计算服务费: 订单总价=" + orderModel.getOrderPrice() + 
                         ", 卖家等级=" + sellerLevel +
                         ", 费率=" + feeInfo.getRate() +
                         ", 服务费=" + feeInfo.getServiceFee() +
                         ", 卖家实收=" + feeInfo.getSellerAmount());
    }

    /**
     * 使用账户余额支付订单（含服务费计算）
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否支付成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payWithBalanceWithServiceFee(Long orderId, Long userId) {
        // 获取订单信息
        OrderModel order = orderDao.selectByPrimaryKey(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 0 || order.getPaymentStatus() != 0) {
            return false;
        }
        
        // 获取卖家信息
        IdleItemModel idleItem = idleItemDao.selectByPrimaryKey(order.getIdleId());
        if (idleItem == null) {
            return false;
        }
        Long sellerId = idleItem.getUserId();
        
        // 计算服务费
        calculateAndSetServiceFee(order, sellerId);
        
        // 扣减买家余额（全额）
        BigDecimal orderAmount = order.getOrderPrice().negate(); // 负数表示扣款
        boolean deductResult = userService.updateUserBalance(userId, orderAmount);
        if (!deductResult) {
            return false;
        }
        
        // 增加卖家余额（实收金额，扣除服务费后）
        boolean addResult = userService.updateUserBalance(sellerId, order.getSellerAmount());
        if (!addResult) {
            return false;
        }
        
        // 更新订单状态（包含服务费信息）
        OrderModel updateOrder = new OrderModel();
        updateOrder.setId(orderId);
        updateOrder.setOrderStatus((byte) 1); // 设置为待发货
        updateOrder.setPaymentStatus((byte) 1); // 设置为已支付
        updateOrder.setPaymentWay("账户余额");
        updateOrder.setPaymentTime(new Date());
        updateOrder.setServiceFee(order.getServiceFee());
        updateOrder.setServiceFeeRate(order.getServiceFeeRate());
        updateOrder.setSellerAmount(order.getSellerAmount());
        
        boolean updateResult = orderDao.updateByPrimaryKeySelective(updateOrder) == 1;
        
        // 如果支付成功，增加商品销量和用户积分
        if (updateResult) {
            Integer purchaseQuantity = order.getPurchaseQuantity() != null ? order.getPurchaseQuantity() : 1;
            idleItemDao.increaseSoldCount(order.getIdleId(), purchaseQuantity);
            
            // 为用户增加积分（积分 = 订单金额的整数部分）
            Integer pointsToAdd = order.getOrderPrice().intValue();
            if (pointsToAdd > 0) {
                userService.updateUserPoints(userId, pointsToAdd);
            }
        }
        
        return updateResult;
    }

    /**
     * 使用账户余额+积分支付订单（含服务费计算）
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param usePoints 使用的积分数量
     * @return 是否支付成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payWithBalanceAndPointsWithServiceFee(Long orderId, Long userId, Integer usePoints) {
        // 获取订单信息
        OrderModel order = orderDao.selectByPrimaryKey(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 0 || order.getPaymentStatus() != 0) {
            return false;
        }
        
        // 检查用户积分是否足够
        Integer userPoints = userService.getUserPoints(userId);
        if (usePoints > userPoints) {
            return false;
        }
        
        // 计算积分抵扣的金额
        BigDecimal pointsDeduction = userService.calculatePointsDeduction(usePoints);
        
        // 确保抵扣金额不超过订单总额
        if (pointsDeduction.compareTo(order.getOrderPrice()) > 0) {
            pointsDeduction = order.getOrderPrice();
        }
        
        // 计算实际需要支付的金额
        BigDecimal actualPayment = order.getOrderPrice().subtract(pointsDeduction);
        
        // 获取卖家信息并计算服务费
        IdleItemModel idleItem = idleItemDao.selectByPrimaryKey(order.getIdleId());
        if (idleItem == null) {
            return false;
        }
        Long sellerId = idleItem.getUserId();
        
        // 计算服务费（基于订单原价）
        calculateAndSetServiceFee(order, sellerId);
        
        // 扣减买家余额（实际支付金额）
        BigDecimal orderAmount = actualPayment.negate(); // 负数表示扣款
        boolean deductResult = userService.updateUserBalance(userId, orderAmount);
        if (!deductResult) {
            return false;
        }
        
        // 增加卖家余额（实收金额，扣除服务费后）
        boolean addResult = userService.updateUserBalance(sellerId, order.getSellerAmount());
        if (!addResult) {
            return false;
        }
        
        // 更新订单状态（包含服务费和积分信息）
        OrderModel updateOrder = new OrderModel();
        updateOrder.setId(orderId);
        updateOrder.setOrderStatus((byte) 1); // 设置为待发货
        updateOrder.setPaymentStatus((byte) 1); // 设置为已支付
        updateOrder.setPaymentWay("账户余额+积分");
        updateOrder.setPaymentTime(new Date());
        updateOrder.setPointsUsed(usePoints);
        updateOrder.setPointsDiscount(pointsDeduction);
        updateOrder.setServiceFee(order.getServiceFee());
        updateOrder.setServiceFeeRate(order.getServiceFeeRate());
        updateOrder.setSellerAmount(order.getSellerAmount());
        
        boolean updateResult = orderDao.updateByPrimaryKeySelective(updateOrder) == 1;
        
        if (updateResult) {
            // 增加商品销量
            Integer purchaseQuantity = order.getPurchaseQuantity() != null ? order.getPurchaseQuantity() : 1;
            idleItemDao.increaseSoldCount(order.getIdleId(), purchaseQuantity);
            
            // 扣减用户积分
            boolean deductPointsResult = userService.deductUserPoints(userId, usePoints, userPoints);
            if (!deductPointsResult) {
                throw new RuntimeException("积分扣减失败");
            }
            
            // 为用户增加新的积分（积分 = 实际支付金额的整数部分）
            Integer pointsToAdd = actualPayment.intValue();
            if (pointsToAdd > 0) {
                userService.updateUserPoints(userId, pointsToAdd);
            }
        }
        
        return updateResult;
    }
}
