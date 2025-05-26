package com.rabbiter.fm.service.impl;

import com.rabbiter.fm.dao.RechargeRecordDao;
import com.rabbiter.fm.model.RechargeRecordModel;
import com.rabbiter.fm.service.RechargeRecordService;
import com.rabbiter.fm.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Resource
    private RechargeRecordDao rechargeRecordDao;
    
    @Resource
    private UserService userService;
    
    /**
     * 创建新的充值记录并更新用户余额
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRechargeRecord(Long userId, BigDecimal amount) {
        // 验证充值金额是否有效
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        // 更新用户余额
        boolean updateResult = userService.updateUserBalance(userId, amount);
        if (!updateResult) {
            return false;
        }
        
        // 创建充值记录
        RechargeRecordModel record = new RechargeRecordModel();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setRechargeTime(new Date());
        record.setStatus((byte) 1); // 1-成功
        
        return rechargeRecordDao.insert(record) == 1;
    }
    
    /**
     * 获取用户的所有充值记录
     * @param userId 用户ID
     * @return 充值记录列表
     */
    @Override
    public List<RechargeRecordModel> getUserRechargeRecords(Long userId) {
        return rechargeRecordDao.getRecordsByUserId(userId);
    }
} 