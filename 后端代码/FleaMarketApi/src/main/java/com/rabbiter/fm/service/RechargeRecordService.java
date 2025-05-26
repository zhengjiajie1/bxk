package com.rabbiter.fm.service;

import com.rabbiter.fm.model.RechargeRecordModel;

import java.math.BigDecimal;
import java.util.List;

public interface RechargeRecordService {
    
    /**
     * 创建新的充值记录
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 是否成功
     */
    boolean createRechargeRecord(Long userId, BigDecimal amount);
    
    /**
     * 获取用户的所有充值记录
     * @param userId 用户ID
     * @return 充值记录列表
     */
    List<RechargeRecordModel> getUserRechargeRecords(Long userId);
} 