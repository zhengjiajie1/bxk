package com.rabbiter.fm.controller;

import com.rabbiter.fm.common.enums.ErrorMsg;
import com.rabbiter.fm.model.OrderAddressModel;
import com.rabbiter.fm.model.OrderModel;
import com.rabbiter.fm.service.IdleItemService;
import com.rabbiter.fm.service.OrderAddressService;
import com.rabbiter.fm.service.OrderService;
import com.rabbiter.fm.service.UserService;
import com.rabbiter.fm.common.utils.IdFactoryUtil;
import com.rabbiter.fm.common.utils.OrderTaskHandler;
import com.rabbiter.fm.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAddressService orderAddressService;

    @Autowired
    private IdleItemService idleItemService;
    
    @Autowired
    private UserService userService;

    /**
     * 新增订单
     * @param shUserId 用户ID
     * @param orderModel 订单模型
     * @return 结果
     */
    @PostMapping("/add")
    public ResultVo addOrder(@CookieValue("shUserId")
                             @NotNull(message = "登录异常 请重新登录")
                             @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                             @RequestBody OrderModel orderModel){
        if(OrderTaskHandler.orderService==null){
            OrderTaskHandler.orderService=orderService;
        }
        orderModel.setOrderNumber(IdFactoryUtil.getOrderId());
        orderModel.setCreateTime(new Date());
        orderModel.setUserId(Long.valueOf(shUserId));
        orderModel.setOrderStatus((byte) 0);
        orderModel.setPaymentStatus((byte)0);
        
        // 处理收货地址
        OrderAddressModel orderAddressModel = orderModel.getOrderAddress();
        if (orderAddressModel != null) {
            orderModel.setOrderAddress(null);
            if(orderService.addOrder(orderModel)){
                orderAddressModel.setOrderId(orderModel.getId());
                orderAddressService.addOrderAddress(orderAddressModel);
                return ResultVo.success(orderModel);
            }
        } else {
            if(orderService.addOrder(orderModel)){
                return ResultVo.success(orderModel);
            }
        }
        
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 根据id获取订单详情
     * @param shUserId 用户ID
     * @param id 订单ID
     * @return 订单信息
     */
    @GetMapping("/info")
    public ResultVo getOrderInfo(@CookieValue("shUserId")
                                 @NotNull(message = "登录异常 请重新登录")
                                 @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                 @RequestParam Long id){
        OrderModel orderModel = orderService.getOrder(id);
        try {
            Long uid = Long.valueOf(shUserId);
            // 订单购买者
            if (uid.equals(orderModel.getUserId())) {
                // 获取商品发布者信息
                orderModel.getIdleItem().setUser(userService.getUser(orderModel.getIdleItem().getUserId()));
                orderModel.setOrderAddress(orderAddressService.getOrderAddress(id));
                return ResultVo.success(orderModel);
            }

            // 订单出售者，出售者查不到地址
            if (uid.equals(orderModel.getIdleItem().getUserId())) {
                orderModel.setOrderAddress(orderAddressService.getOrderAddress(id));
                return ResultVo.success(orderModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 更新订单状态
     * @param shUserId 用户ID
     * @param orderModel 订单模型
     * @return 结果
     */
    @PostMapping("/update")
    public ResultVo updateOrder(@CookieValue("shUserId")
                             @NotNull(message = "登录异常 请重新登录")
                             @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                             @RequestBody OrderModel orderModel){
        if(orderModel.getPaymentStatus()!=null && orderModel.getPaymentStatus().equals((byte) 1)){
            orderModel.setPaymentTime(new Date());
        }
        if(orderService.updateOrder(orderModel)){
            return ResultVo.success(orderModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 获取我的订单列表
     * @param shUserId 用户ID
     * @return 订单列表
     */
    @GetMapping("/my")
    public ResultVo getMyOrder(@CookieValue("shUserId")
                                 @NotNull(message = "登录异常 请重新登录")
                                 @NotEmpty(message = "登录异常 请重新登录") String shUserId){
        return ResultVo.success(orderService.getMyOrder(Long.valueOf(shUserId)));
    }

    /**
     * 获取我卖出的商品
     * @param shUserId 用户ID
     * @return 订单列表
     */
    @GetMapping("/my-sold")
    public ResultVo getMySoldIdle(@CookieValue("shUserId")
                               @NotNull(message = "登录异常 请重新登录")
                               @NotEmpty(message = "登录异常 请重新登录") String shUserId){
        return ResultVo.success(orderService.getMySoldIdle(Long.valueOf(shUserId)));
    }
    
    /**
     * 用账户余额支付订单
     * @param id 用户ID
     * @param orderId 订单ID
     * @return 支付结果
     */
    @PostMapping("/pay-with-balance")
    public ResultVo payWithBalance(@CookieValue("shUserId") 
                                  @NotNull(message = "登录异常 请重新登录")
                                  @NotEmpty(message = "登录异常 请重新登录") String id,
                                  @RequestParam("orderId") Long orderId) {
        Long userId = Long.valueOf(id);
        
        // 获取订单信息
        OrderModel order = orderService.getOrder(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 0 || order.getPaymentStatus() != 0) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("订单状态不允许支付"));
        }
        
        // 检查余额是否足够
        BigDecimal balance = userService.getUserBalance(userId);
        if (balance.compareTo(order.getOrderPrice()) < 0) {
            return ResultVo.fail(ErrorMsg.INSUFFICIENT_BALANCE);
        }
        
        // 使用余额支付
        if (orderService.payWithBalance(orderId, userId)) {
            return ResultVo.success();
        }
        
        return ResultVo.fail(ErrorMsg.PAYMENT_FAILED);
    }
    
    /**
     * 使用账户余额+积分支付订单
     * @param id 用户ID
     * @param orderId 订单ID
     * @param usePoints 使用的积分数量
     * @return 支付结果
     */
    @PostMapping("/pay-with-points")
    public ResultVo payWithBalanceAndPoints(
            @CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录") @NotEmpty(message = "登录异常 请重新登录") String id,
            @RequestParam("orderId") Long orderId,
            @RequestParam("usePoints") Integer usePoints) {
        
        Long userId = Long.valueOf(id);
        System.out.println("积分支付请求: userId=" + userId + ", orderId=" + orderId + ", usePoints=" + usePoints);
        
        if (usePoints < 0) {
            System.err.println("积分支付错误: 积分数量为负数, usePoints=" + usePoints);
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("积分数量不能为负数"));
        }
        
        // 验证积分是否为100的整数倍
        if (usePoints % 100 != 0) {
            System.err.println("积分支付错误: 积分不是100的整数倍, usePoints=" + usePoints);
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("积分必须为100的整数倍"));
        }
        
        // 验证积分是否至少为100
        if (usePoints > 0 && usePoints < 100) {
            System.err.println("积分支付错误: 积分少于100, usePoints=" + usePoints);
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("最少使用100积分"));
        }
        
        // 获取订单信息
        OrderModel order = orderService.getOrder(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            System.err.println("积分支付错误: 订单不存在或不属于当前用户, orderId=" + orderId + ", userId=" + userId);
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 0 || order.getPaymentStatus() != 0) {
            System.err.println("积分支付错误: 订单状态不允许支付, orderStatus=" + order.getOrderStatus() + ", paymentStatus=" + order.getPaymentStatus());
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("订单状态不允许支付"));
        }
        
        try {
            // 获取用户积分
            Integer userPoints = userService.getUserPoints(userId);
            if (userPoints < usePoints) {
                System.err.println("积分支付错误: 用户积分不足, userPoints=" + userPoints + ", usePoints=" + usePoints);
                return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("积分不足"));
            }
            
            // 计算积分抵扣金额
            BigDecimal pointsDeduction = userService.calculatePointsDeduction(usePoints);
            
            // 确保抵扣金额不超过订单总额
            if (pointsDeduction.compareTo(order.getOrderPrice()) > 0) {
                System.out.println("积分抵扣金额超过订单总额, 抵扣金额调整为订单总额");
                pointsDeduction = order.getOrderPrice();
            }
            
            // 计算实际需要支付的金额
            BigDecimal actualPayment = order.getOrderPrice().subtract(pointsDeduction);
            System.out.println("积分支付计算: 订单总额=" + order.getOrderPrice() + ", 积分抵扣=" + pointsDeduction + ", 实际支付=" + actualPayment);
            
            // 检查余额是否足够
            BigDecimal balance = userService.getUserBalance(userId);
            if (balance.compareTo(actualPayment) < 0) {
                System.err.println("积分支付错误: 余额不足, balance=" + balance + ", actualPayment=" + actualPayment);
                return ResultVo.fail(ErrorMsg.INSUFFICIENT_BALANCE);
            }
            
            // 使用积分+余额支付
            boolean payResult = orderService.payWithBalanceAndPoints(orderId, userId, usePoints);
            if (payResult) {
                System.out.println("积分支付成功: orderId=" + orderId + ", userId=" + userId + ", usePoints=" + usePoints);
                
                // 获取最新订单信息
                OrderModel updatedOrder = orderService.getOrder(orderId);
                
                Map<String, Object> result = new HashMap<>();
                result.put("orderId", orderId);
                result.put("orderPrice", order.getOrderPrice());
                result.put("pointsUsed", usePoints);
                result.put("pointsDeduction", pointsDeduction);
                result.put("actualPayment", actualPayment);
                
                // 如果有更新后的订单信息，添加到结果中
                if (updatedOrder != null) {
                    result.put("paymentWay", updatedOrder.getPaymentWay());
                    result.put("paymentTime", updatedOrder.getPaymentTime());
                    result.put("orderStatus", updatedOrder.getOrderStatus());
                }
                
                return ResultVo.success(result);
            } else {
                System.err.println("积分支付失败: orderId=" + orderId + ", userId=" + userId + ", usePoints=" + usePoints);
                return ResultVo.fail(ErrorMsg.PAYMENT_FAILED);
            }
        } catch (Exception e) {
            System.err.println("积分支付异常: " + e.getMessage());
            e.printStackTrace();
            return ResultVo.fail(ErrorMsg.PAYMENT_FAILED.withMsg("支付过程中发生异常: " + e.getMessage()));
        }
    }

    /**
     * 获取订单详情 (无需登录检查，由评价服务检查权限)
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/detail")
    public ResultVo getOrderDetail(@RequestParam Long orderId) {
        OrderModel orderModel = orderService.getOrder(orderId);
        if (orderModel != null) {
            // 获取关联的商品信息
            orderModel.getIdleItem().setUser(userService.getUser(orderModel.getIdleItem().getUserId()));
            // 获取关联的地址信息
            orderModel.setOrderAddress(orderAddressService.getOrderAddress(orderId));
            return ResultVo.success(orderModel);
        }
        return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("订单不存在"));
    }

    // 新增：带服务费的支付接口
    /**
     * 用账户余额支付订单（含服务费计算）
     * @param id 用户ID
     * @param orderId 订单ID
     * @return 支付结果
     */
    @PostMapping("/pay-with-balance-service")
    public ResultVo payWithBalanceWithService(@CookieValue("shUserId") 
                                             @NotNull(message = "登录异常 请重新登录")
                                             @NotEmpty(message = "登录异常 请重新登录") String id,
                                             @RequestParam("orderId") Long orderId) {
        Long userId = Long.valueOf(id);
        
        // 获取订单信息
        OrderModel order = orderService.getOrder(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 0 || order.getPaymentStatus() != 0) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("订单状态不允许支付"));
        }
        
        // 检查余额是否足够
        BigDecimal balance = userService.getUserBalance(userId);
        if (balance.compareTo(order.getOrderPrice()) < 0) {
            return ResultVo.fail(ErrorMsg.INSUFFICIENT_BALANCE);
        }
        
        // 使用带服务费的余额支付
        if (orderService.payWithBalanceWithServiceFee(orderId, userId)) {
            return ResultVo.success();
        }
        
        return ResultVo.fail(ErrorMsg.PAYMENT_FAILED);
    }

    /**
     * 使用账户余额+积分支付订单（含服务费计算）
     * @param id 用户ID
     * @param orderId 订单ID
     * @param usePoints 使用的积分数量
     * @return 支付结果
     */
    @PostMapping("/pay-with-points-service")
    public ResultVo payWithBalanceAndPointsWithService(@CookieValue("shUserId") 
                                                      @NotNull(message = "登录异常 请重新登录")
                                                      @NotEmpty(message = "登录异常 请重新登录") String id,
                                                      @RequestParam("orderId") Long orderId,
                                                      @RequestParam("usePoints") Integer usePoints) {
        Long userId = Long.valueOf(id);
        
        // 验证积分
        if (usePoints < 0) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("积分数量不能为负数"));
        }
        if (usePoints % 100 != 0) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("积分必须为100的整数倍"));
        }
        if (usePoints > 0 && usePoints < 100) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("最少使用100积分"));
        }
        
        // 获取订单信息
        OrderModel order = orderService.getOrder(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 0 || order.getPaymentStatus() != 0) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("订单状态不允许支付"));
        }
        
        try {
            // 获取用户积分
            Integer userPoints = userService.getUserPoints(userId);
            if (userPoints < usePoints) {
                return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("积分不足"));
            }
            
            // 计算积分抵扣金额
            BigDecimal pointsDeduction = userService.calculatePointsDeduction(usePoints);
            
            // 确保抵扣金额不超过订单总额
            if (pointsDeduction.compareTo(order.getOrderPrice()) > 0) {
                pointsDeduction = order.getOrderPrice();
            }
            
            // 计算实际需要支付的金额
            BigDecimal actualPayment = order.getOrderPrice().subtract(pointsDeduction);
            
            // 检查余额是否足够
            BigDecimal balance = userService.getUserBalance(userId);
            if (balance.compareTo(actualPayment) < 0) {
                return ResultVo.fail(ErrorMsg.INSUFFICIENT_BALANCE);
            }
            
            // 使用带服务费的积分+余额支付
            boolean payResult = orderService.payWithBalanceAndPointsWithServiceFee(orderId, userId, usePoints);
            if (payResult) {
                // 获取最新订单信息
                OrderModel updatedOrder = orderService.getOrder(orderId);
                
                Map<String, Object> result = new HashMap<>();
                result.put("orderId", orderId);
                result.put("orderPrice", order.getOrderPrice());
                result.put("pointsUsed", usePoints);
                result.put("pointsDeduction", pointsDeduction);
                result.put("actualPayment", actualPayment);
                
                // 如果有更新后的订单信息，添加到结果中
                if (updatedOrder != null) {
                    result.put("paymentWay", updatedOrder.getPaymentWay());
                    result.put("paymentTime", updatedOrder.getPaymentTime());
                    result.put("orderStatus", updatedOrder.getOrderStatus());
                    result.put("serviceFee", updatedOrder.getServiceFee());
                    result.put("serviceFeeRate", updatedOrder.getServiceFeeRate());
                    result.put("sellerAmount", updatedOrder.getSellerAmount());
                }
                
                return ResultVo.success(result);
            } else {
                return ResultVo.fail(ErrorMsg.PAYMENT_FAILED);
            }
        } catch (Exception e) {
            System.err.println("服务费支付异常: " + e.getMessage());
            e.printStackTrace();
            return ResultVo.fail(ErrorMsg.PAYMENT_FAILED.withMsg("支付过程中发生异常: " + e.getMessage()));
        }
    }
}
