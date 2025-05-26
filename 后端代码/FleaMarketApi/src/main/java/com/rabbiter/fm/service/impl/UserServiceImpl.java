package com.rabbiter.fm.service.impl;

import com.rabbiter.fm.service.UserService;
import com.rabbiter.fm.dao.UserDao;
import com.rabbiter.fm.model.UserModel;
import com.rabbiter.fm.vo.PageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;



@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    /**
     * 查询一个用户的公开信息
     * @param id
     * @return
     */
    public UserModel getUser(Long id){
        return userDao.selectByPrimaryKey(id);
    }

    /**
     * 登录，安全问题未解决
     * @param accountNumber
     * @param userPassword
     * @return
     */
    public UserModel userLogin(String accountNumber, String userPassword){
        return userDao.userLogin(accountNumber,userPassword);
    }

    /**
     *注册
     * @param userModel
     * @return
     */
    public boolean userSignIn(UserModel userModel){
        // 确保积分值不为null
        if (userModel.getConsumerPoints() == null) {
            userModel.setConsumerPoints(0);
        }
        return userDao.insert(userModel) == 1;
    }

    /**
     *修改用户公开信息，未验证用户身份
     * @param userModel
     * @return
     */
    public boolean updateUserInfo(UserModel userModel){
        // 如果未指定积分，则保留原有积分
        if (userModel.getConsumerPoints() == null) {
            UserModel currentUser = userDao.selectByPrimaryKey(userModel.getId());
            if (currentUser != null) {
                userModel.setConsumerPoints(currentUser.getConsumerPoints());
                System.out.println("保留原有积分: 用户ID=" + userModel.getId() + ", 积分=" + currentUser.getConsumerPoints());
            }
        } else {
            System.out.println("更新用户积分: 用户ID=" + userModel.getId() + ", 新积分=" + userModel.getConsumerPoints());
        }
        return userDao.updateByPrimaryKeySelective(userModel)==1;
    }

    /**
     * 修改密码
     * @param newPassword
     * @param oldPassword
     * @param id
     * @return
     */
    public boolean updatePassword(String newPassword, String oldPassword,Long id){
        return userDao.updatePassword(newPassword,oldPassword,id)==1;
    }

    public PageVo<UserModel> getUserByStatus(int status,int page ,int nums){
        List<UserModel> list;
        int count=0;
        if(status==0){
            count=userDao.countNormalUser();
            list=userDao.getNormalUser((page-1)*nums, nums);
        }else if(status==1){
            count=userDao.countBanUser();
            list=userDao.getBanUser((page-1)*nums, nums);
        }else if(status==2){
            count=userDao.countPendingUser();
            list=userDao.getPendingUser((page-1)*nums, nums);
        }else {
            count=userDao.countNormalUser();
            list=userDao.getNormalUser((page-1)*nums, nums);
        }
        return new PageVo<>(list,count);
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteUser(Long id) {
        return userDao.deleteByPrimaryKey(id) == 1;
    }
    
    /**
     * 获取用户账户余额
     * @param userId 用户ID
     * @return 用户当前余额
     */
    @Override
    public BigDecimal getUserBalance(Long userId) {
        UserModel user = userDao.selectByPrimaryKey(userId);
        if (user == null || user.getAccountBalance() == null) {
            return BigDecimal.ZERO;
        }
        return user.getAccountBalance();
    }
    
    /**
     * 更新用户余额
     * @param userId 用户ID
     * @param amount 金额（正数增加，负数减少）
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserBalance(Long userId, BigDecimal amount) {
        UserModel user = userDao.selectByPrimaryKey(userId);
        if (user == null) {
            return false;
        }
        
        // 计算新余额
        BigDecimal currentBalance = user.getAccountBalance();
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
        
        BigDecimal newBalance = currentBalance.add(amount);
        
        // 如果扣款，检查余额是否足够
        if (amount.compareTo(BigDecimal.ZERO) < 0 && 
            newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        
        // 更新余额，同时保留原有积分
        UserModel updateModel = new UserModel();
        updateModel.setId(userId);
        updateModel.setAccountBalance(newBalance);
        
        // 获取并保留当前积分，确保不会重置积分
        Integer currentPoints = user.getConsumerPoints();
        if (currentPoints != null) {
            updateModel.setConsumerPoints(currentPoints);
            System.out.println("更新余额时保留积分: 用户ID=" + userId + ", 积分=" + currentPoints);
        }
        
        // 获取并保留当前用户等级，确保不会重置等级
        Byte currentUserLevel = user.getUserLevel();
        if (currentUserLevel != null) {
            updateModel.setUserLevel(currentUserLevel);
            System.out.println("更新余额时保留用户等级: 用户ID=" + userId + ", 等级=" + currentUserLevel);
        }
        
        return userDao.updateByPrimaryKeySelective(updateModel) == 1;
    }
    
    /**
     * 获取用户消费积分
     * @param userId 用户ID
     * @return 用户当前积分
     */
    @Override
    public Integer getUserPoints(Long userId) {
        UserModel user = userDao.selectByPrimaryKey(userId);
        Integer points = 0;
        if (user == null || user.getConsumerPoints() == null) {
            System.out.println("积分查询: 用户ID=" + userId + ", 查询结果=0 (用户不存在或积分为空)");
            return 0;
        } else {
            points = user.getConsumerPoints();
            System.out.println("积分查询: 用户ID=" + userId + ", 查询结果=" + points);
        }
        return points;
    }
    
    /**
     * 更新用户积分
     * @param userId 用户ID
     * @param points 积分值（正数增加，负数减少）
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserPoints(Long userId, Integer points) {
        // 不再限制积分必须为正数，负数表示扣减积分
        if (points == 0) {
            return true; // 积分为0，无需更新
        }
        
        UserModel user = userDao.selectByPrimaryKey(userId);
        if (user == null) {
            return false;
        }
        
        // 计算新积分
        Integer currentPoints = user.getConsumerPoints();
        if (currentPoints == null) {
            currentPoints = 0;
        }
        
        Integer newPoints = currentPoints + points;
        
        // 如果扣减积分，检查积分是否足够
        if (points < 0 && newPoints < 0) {
            return false; // 积分不足
        }
        
        // 更新积分，同时保留原有余额
        UserModel updateModel = new UserModel();
        updateModel.setId(userId);
        updateModel.setConsumerPoints(newPoints);
        
        // 获取并保留当前余额，确保不会重置余额
        BigDecimal currentBalance = user.getAccountBalance();
        if (currentBalance != null) {
            updateModel.setAccountBalance(currentBalance);
            System.out.println("更新积分时保留余额: 用户ID=" + userId + ", 余额=" + currentBalance);
        }
        
        return userDao.updateByPrimaryKeySelective(updateModel) == 1;
    }
    
    /**
     * 计算积分可抵扣的金额
     * @param points 要使用的积分数量
     * @return 可抵扣的金额
     */
    @Override
    public BigDecimal calculatePointsDeduction(Integer points) {
        if (points == null || points <= 0) {
            System.out.println("积分抵扣计算: 无效积分值 points=" + points);
            return BigDecimal.ZERO;
        }
        
        // 确保积分是100的整数倍
        if (points % 100 != 0) {
            System.out.println("积分抵扣计算: 积分不是100的整数倍 points=" + points);
            return BigDecimal.ZERO;
        }
        
        // 每100积分抵扣1元
        BigDecimal deduction = new BigDecimal(points).divide(new BigDecimal(100), 2, BigDecimal.ROUND_DOWN);
        System.out.println("积分抵扣计算: 积分=" + points + ", 抵扣金额=" + deduction);
        return deduction;
    }

    /**
     * 扣减用户积分
     * @param userId 用户ID
     * @param points 要扣减的积分数量（正数）
     * @param currentPoints 用户当前的积分数量，避免再次查询
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductUserPoints(Long userId, Integer points, Integer currentPoints) {
        if (points <= 0) {
            System.err.println("积分扣减失败: 扣减积分必须为正数, points=" + points);
            return false; // 扣减积分必须为正数
        }
        
        // 确保积分是100的整数倍
        if (points % 100 != 0) {
            System.err.println("积分扣减失败: 积分必须为100的整数倍, points=" + points);
            return false; // 积分必须为100的整数倍
        }
        
        // 使用传入的积分值，避免再次查询数据库
        System.out.println("积分扣减: 用户ID=" + userId + ", 传入的当前积分=" + currentPoints + ", 扣减积分=" + points);
        
        // 检查积分是否足够
        if (currentPoints < points) {
            System.err.println("积分扣减失败: 积分不足, 当前积分=" + currentPoints + ", 需要扣减积分=" + points);
            return false; // 积分不足
        }
        
        // 计算新积分
        Integer newPoints = currentPoints - points;
        
        // 获取用户信息，以保留其他字段（如余额）
        UserModel user = userDao.selectByPrimaryKey(userId);
        if (user == null) {
            System.err.println("积分扣减失败: 用户不存在");
            return false;
        }
        
        // 更新积分，同时保留原有余额
        UserModel updateModel = new UserModel();
        updateModel.setId(userId);
        updateModel.setConsumerPoints(newPoints);
        
        // 获取并保留当前余额，确保不会重置余额
        BigDecimal currentBalance = user.getAccountBalance();
        if (currentBalance != null) {
            updateModel.setAccountBalance(currentBalance);
            System.out.println("扣减积分时保留余额: 用户ID=" + userId + ", 余额=" + currentBalance);
        }
        
        boolean result = userDao.updateByPrimaryKeySelective(updateModel) == 1;
        if (!result) {
            System.err.println("积分扣减失败: 数据库更新失败");
        } else {
            System.out.println("积分扣减成功: 用户ID=" + userId + ", 扣减前积分=" + currentPoints + ", 扣减后积分=" + newPoints);
        }
        return result;
    }

    // 新增：用户等级管理相关方法
    /**
     * 获取用户等级
     * @param userId 用户ID
     * @return 用户等级
     */
    @Override
    public Byte getUserLevel(Long userId) {
        UserModel user = userDao.selectByPrimaryKey(userId);
        if (user == null) {
            return (byte) 1; // 默认等级1
        }
        return user.getUserLevel();
    }

    /**
     * 更新用户等级（管理员功能）
     * @param userId 用户ID
     * @param userLevel 新的用户等级（1-5）
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserLevel(Long userId, Byte userLevel) {
        // 验证等级范围
        if (userLevel == null || userLevel < 1 || userLevel > 5) {
            System.err.println("用户等级更新失败: 等级值无效, userLevel=" + userLevel);
            return false;
        }
        
        UserModel user = userDao.selectByPrimaryKey(userId);
        if (user == null) {
            System.err.println("用户等级更新失败: 用户不存在, userId=" + userId);
            return false;
        }
        
        // 更新用户等级，同时保留其他字段
        UserModel updateModel = new UserModel();
        updateModel.setId(userId);
        updateModel.setUserLevel(userLevel);
        
        // 保留原有余额和积分
        if (user.getAccountBalance() != null) {
            updateModel.setAccountBalance(user.getAccountBalance());
        }
        if (user.getConsumerPoints() != null) {
            updateModel.setConsumerPoints(user.getConsumerPoints());
        }
        
        boolean result = userDao.updateByPrimaryKeySelective(updateModel) == 1;
        if (result) {
            System.out.println("用户等级更新成功: 用户ID=" + userId + ", 新等级=" + userLevel);
        } else {
            System.err.println("用户等级更新失败: 数据库更新失败");
        }
        return result;
    }
}
