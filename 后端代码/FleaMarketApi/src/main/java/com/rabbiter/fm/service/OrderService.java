package com.rabbiter.fm.service;

import com.rabbiter.fm.model.OrderModel;
import com.rabbiter.fm.vo.PageVo;

import java.util.List;

public interface OrderService {

    /**
     * 新增订单
     * @param orderModel
     * @return
     */
    boolean addOrder(OrderModel orderModel);

    /**
     * 获取订单信息
     * @param id
     * @return
     */
    OrderModel getOrder(Long id);

    /**
     * 更新订单信息
     * @param orderModel
     * @return
     */
    boolean updateOrder(OrderModel orderModel);

    /**
     * 获取某个用户买到的闲置的订单列表
     * @param userId
     * @return
     */
    List<OrderModel> getMyOrder(Long userId);

    /**
     * 获取某个用户卖出的闲置的订单信息
     * @param userId
     * @return
     */
    List<OrderModel> getMySoldIdle(Long userId);

    PageVo<OrderModel> getAllOrder(int page, int nums);

    boolean deleteOrder(long id);
    
    /**
     * 使用账户余额支付订单
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否支付成功
     */
    boolean payWithBalance(Long orderId, Long userId);
    
    /**
     * 使用账户余额支付订单（带积分抵扣）
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param usePoints 使用的积分数量
     * @return 是否支付成功
     */
    boolean payWithBalanceAndPoints(Long orderId, Long userId, Integer usePoints);

    // 新增：服务费计算相关方法
    /**
     * 计算并设置订单的服务费信息
     * @param orderModel 订单模型
     * @param sellerId 卖家ID
     */
    void calculateAndSetServiceFee(OrderModel orderModel, Long sellerId);

    /**
     * 使用账户余额支付订单（含服务费计算）
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否支付成功
     */
    boolean payWithBalanceWithServiceFee(Long orderId, Long userId);

    /**
     * 使用账户余额+积分支付订单（含服务费计算）
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param usePoints 使用的积分数量
     * @return 是否支付成功
     */
    boolean payWithBalanceAndPointsWithServiceFee(Long orderId, Long userId, Integer usePoints);
}
