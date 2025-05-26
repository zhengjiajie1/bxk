package com.rabbiter.fm.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 服务费计算工具类
 * @author rabbiter
 */
public class ServiceFeeCalculator {

    /**
     * 根据用户等级获取服务费费率
     * @param userLevel 用户等级 (1-5)
     * @return 服务费费率
     */
    public static BigDecimal getServiceFeeRate(Byte userLevel) {
        if (userLevel == null) {
            userLevel = 1; // 默认等级1
        }
        
        switch (userLevel) {
            case 1:
                return new BigDecimal("0.001"); // 0.1%
            case 2:
                return new BigDecimal("0.002"); // 0.2%
            case 3:
                return new BigDecimal("0.005"); // 0.5%
            case 4:
                return new BigDecimal("0.0075"); // 0.75%
            case 5:
                return new BigDecimal("0.01"); // 1%
            default:
                return new BigDecimal("0.001"); // 默认0.1%
        }
    }

    /**
     * 计算服务费
     * @param orderPrice 订单总价
     * @param userLevel 用户等级
     * @return 服务费金额
     */
    public static BigDecimal calculateServiceFee(BigDecimal orderPrice, Byte userLevel) {
        if (orderPrice == null || orderPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal rate = getServiceFeeRate(userLevel);
        return orderPrice.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算卖家实收金额
     * @param orderPrice 订单总价
     * @param serviceFee 服务费
     * @return 卖家实收金额
     */
    public static BigDecimal calculateSellerAmount(BigDecimal orderPrice, BigDecimal serviceFee) {
        if (orderPrice == null) {
            return BigDecimal.ZERO;
        }
        if (serviceFee == null) {
            serviceFee = BigDecimal.ZERO;
        }
        
        return orderPrice.subtract(serviceFee).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 一次性计算所有费用相关信息
     * @param orderPrice 订单总价
     * @param userLevel 用户等级
     * @return 包含费率、服务费、卖家实收金额的数组
     */
    public static ServiceFeeInfo calculateAllFees(BigDecimal orderPrice, Byte userLevel) {
        BigDecimal rate = getServiceFeeRate(userLevel);
        BigDecimal serviceFee = calculateServiceFee(orderPrice, userLevel);
        BigDecimal sellerAmount = calculateSellerAmount(orderPrice, serviceFee);
        
        return new ServiceFeeInfo(rate, serviceFee, sellerAmount);
    }

    /**
     * 服务费信息封装类
     */
    public static class ServiceFeeInfo {
        private BigDecimal rate;
        private BigDecimal serviceFee;
        private BigDecimal sellerAmount;

        public ServiceFeeInfo(BigDecimal rate, BigDecimal serviceFee, BigDecimal sellerAmount) {
            this.rate = rate;
            this.serviceFee = serviceFee;
            this.sellerAmount = sellerAmount;
        }

        public BigDecimal getRate() {
            return rate;
        }

        public BigDecimal getServiceFee() {
            return serviceFee;
        }

        public BigDecimal getSellerAmount() {
            return sellerAmount;
        }
    }
} 