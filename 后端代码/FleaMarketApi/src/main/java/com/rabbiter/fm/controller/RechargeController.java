package com.rabbiter.fm.controller;

import com.rabbiter.fm.common.enums.ErrorMsg;
import com.rabbiter.fm.model.RechargeRecordModel;
import com.rabbiter.fm.model.UserModel;
import com.rabbiter.fm.service.RechargeRecordService;
import com.rabbiter.fm.service.UserService;
import com.rabbiter.fm.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("recharge")
public class RechargeController {

    @Autowired
    private RechargeRecordService rechargeRecordService;
    
    @Autowired
    private UserService userService;

    /**
     * 充值接口
     * @param shUserId 用户ID
     * @param requestBody 包含amount参数的请求体
     * @return 充值结果
     */
    @PostMapping("/create")
    public ResultVo createRecharge(
            @CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录") @NotEmpty(message = "登录异常 请重新登录") String shUserId,
            @RequestBody Map<String, String> requestBody) {
        
        try {
            // 从请求体中获取amount参数
            String amount = requestBody.get("amount");
            if (amount == null || amount.isEmpty()) {
                return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("充值金额不能为空"));
            }
            
            // 验证金额
            BigDecimal rechargeAmount = new BigDecimal(amount);
            
            // 验证金额范围
            if (rechargeAmount.compareTo(BigDecimal.ZERO) <= 0 || 
                rechargeAmount.compareTo(new BigDecimal("999999.00")) > 0) {
                return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("充值金额必须在0.01-999999.00之间"));
            }
            
            // 执行充值
            boolean result = rechargeRecordService.createRechargeRecord(Long.valueOf(shUserId), rechargeAmount);
            if (result) {
                return ResultVo.success();
            } else {
                return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("充值失败，请稍后重试"));
            }
        } catch (NumberFormatException e) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("充值金额格式不正确"));
        }
    }
    
    /**
     * 获取用户的充值记录
     * @param shUserId 用户ID
     * @return 充值记录列表
     */
    @GetMapping("/records")
    public ResultVo getRechargeRecords(
            @CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录") @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        
        List<RechargeRecordModel> records = rechargeRecordService.getUserRechargeRecords(Long.valueOf(shUserId));
        return ResultVo.success(records);
    }
    
    /**
     * 获取用户积分
     * @param shUserId 用户ID
     * @return 用户积分
     */
    @GetMapping("/points")
    public ResultVo getUserPoints(
            @CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录") @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        
        Integer points = userService.getUserPoints(Long.valueOf(shUserId));
        return ResultVo.success(points);
    }
    
    /**
     * 计算积分可抵扣金额
     * @param points 积分数量
     * @return 可抵扣金额
     */
    @GetMapping("/calculate-deduction")
    public ResultVo calculatePointsDeduction(@RequestParam("points") Integer points) {
        if (points == null || points < 0) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("积分数量必须大于等于0"));
        }
        
        BigDecimal deduction = userService.calculatePointsDeduction(points);
        
        Map<String, Object> result = new HashMap<>();
        result.put("points", points);
        result.put("deduction", deduction);
        
        return ResultVo.success(result);
    }
    
    /**
     * 测试用：修复用户积分数据
     * @param userId 用户ID
     * @param points 要设置的积分值
     * @return 设置结果
     */
    @GetMapping("/fix-points")
    public ResultVo fixUserPoints(
            @RequestParam("userId") Long userId,
            @RequestParam("points") Integer points) {
        
        if (points < 0) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("积分不能为负数"));
        }
        
        // 直接获取当前用户对象
        UserModel user = userService.getUser(userId);
        if (user == null) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("用户不存在"));
        }
        
        // 创建用于更新的对象
        UserModel updateModel = new UserModel();
        updateModel.setId(userId);
        updateModel.setConsumerPoints(points);
        
        // 直接更新数据库
        boolean result = userService.updateUserInfo(updateModel);
        
        if (result) {
            // 再次查询确认更新成功
            Integer newPoints = userService.getUserPoints(userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("oldPoints", user.getConsumerPoints());
            data.put("newPoints", newPoints);
            
            return ResultVo.success(data);
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR.withMsg("积分修复失败"));
        }
    }
} 