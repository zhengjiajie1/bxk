package com.rabbiter.fm.service;

import com.rabbiter.fm.model.UserModel;
import com.rabbiter.fm.vo.PageVo;

import java.math.BigDecimal;

public interface UserService {

    /**
     * 获取某个用户的公开信息
     * @param id
     * @return
     */
    UserModel getUser(Long id);

    /**
     * 登录接口
     * @param accountNumber
     * @param userPassword
     * @return
     */
    UserModel userLogin(String accountNumber, String userPassword);

    /**
     * 注册接口
     * @param userModel
     * @return
     */
    boolean userSignIn(UserModel userModel);

    /**
     * 更新用户信息
     * @param userModel
     * @return
     */
    boolean updateUserInfo(UserModel userModel);

    /**
     * 修改密码
     * @param newPassword
     * @param oldPassword
     * @param id
     * @return
     */
    boolean updatePassword(String newPassword, String oldPassword,Long id);

    PageVo<UserModel> getUserByStatus(int status, int page , int nums);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);
    
    /**
     * 获取用户账户余额
     * @param userId 用户ID
     * @return 用户当前余额
     */
    BigDecimal getUserBalance(Long userId);
    
    /**
     * 更新用户余额
     * @param userId 用户ID
     * @param amount 金额（正数增加，负数减少）
     * @return 是否成功
     */
    boolean updateUserBalance(Long userId, BigDecimal amount);
    
    /**
     * 获取用户消费积分
     * @param userId 用户ID
     * @return 用户当前积分
     */
    Integer getUserPoints(Long userId);
    
    /**
     * 更新用户积分
     * @param userId 用户ID
     * @param points 积分值（正数增加）
     * @return 是否成功
     */
    boolean updateUserPoints(Long userId, Integer points);
    
    /**
     * 计算积分可抵扣的金额
     * @param points 要使用的积分数量
     * @return 可抵扣的金额
     */
    BigDecimal calculatePointsDeduction(Integer points);

    /**
     * 扣减用户积分
     * @param userId 用户ID
     * @param points 要扣减的积分数量（正数）
     * @param currentPoints 用户当前的积分数量，避免再次查询
     * @return 是否成功
     */
    boolean deductUserPoints(Long userId, Integer points, Integer currentPoints);

    // 新增：用户等级管理相关方法
    /**
     * 获取用户等级
     * @param userId 用户ID
     * @return 用户等级
     */
    Byte getUserLevel(Long userId);

    /**
     * 更新用户等级（管理员功能）
     * @param userId 用户ID
     * @param userLevel 新的用户等级（1-5）
     * @return 是否成功
     */
    boolean updateUserLevel(Long userId, Byte userLevel);
}
