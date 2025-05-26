package com.rabbiter.fm.controller;

import com.rabbiter.fm.dto.UserRegisterDTO;
import com.rabbiter.fm.service.CaptchaService;
import com.rabbiter.fm.service.UserService;
import com.rabbiter.fm.common.enums.ErrorMsg;
import com.rabbiter.fm.model.UserModel;
import com.rabbiter.fm.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;

@CrossOrigin
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private CaptchaService captchaService;

    /**
     * 注册账号
     * @param userRegisterDTO 用户注册DTO，包含用户信息和验证码
     * @return 注册结果
     */
    @PostMapping("sign-in")
    public ResultVo signIn(@RequestBody UserRegisterDTO userRegisterDTO) {
        // 验证验证码
        if (userRegisterDTO.getCaptchaId() == null || userRegisterDTO.getCaptchaText() == null) {
            return ResultVo.fail(ErrorMsg.CAPTCHA_EMPTY);
        }
        
        boolean isValidCaptcha = captchaService.validateCaptcha(userRegisterDTO.getCaptchaId(), userRegisterDTO.getCaptchaText());
        if (!isValidCaptcha) {
            return ResultVo.fail(ErrorMsg.CAPTCHA_ERROR);
        }
        
        // 获取用户信息
        UserModel userModel = userRegisterDTO.getUserModel();
        if (userModel == null) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        System.out.println(userModel);
        userModel.setSignInTime(new Timestamp(System.currentTimeMillis()));
        if (userModel.getAvatar() == null || "".equals(userModel.getAvatar())) {
            userModel.setAvatar("/image?imageName=noasndo123.jpg");
        }
        // 设置默认值
        if (userModel.getGender() == null) {
            userModel.setGender("男");
        }
        if (userModel.getCity() == null) {
            userModel.setCity("北京");
        }
        // 验证银行卡号
        if (userModel.getBankCard() != null && userModel.getBankCard().length() != 16) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("银行卡号必须为16位"));
        }
        // 验证邮箱格式
        if (userModel.getEmail() != null && !userModel.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("邮箱格式不正确"));
        }
        // 确保设置用户状态，防止数据库中出现null值
        if (userModel.getUserStatus() == null) {
            // 默认设置为待审核状态(2)
            userModel.setUserStatus((byte) 2);
        }
        // 设置初始余额为0
        userModel.setAccountBalance(BigDecimal.ZERO);
        
        // 设置初始积分为0
        userModel.setConsumerPoints(0);
        
        if (userService.userSignIn(userModel)) {
            return ResultVo.success(userModel);
        }
        return ResultVo.fail(ErrorMsg.REGISTER_ERROR);
    }

    /**
     * 登录
     *
     * @param accountNumber
     * @param userPassword
     * @param response
     * @return
     */
    @RequestMapping("login")
    public ResultVo login(@RequestParam("accountNumber") @NotEmpty @NotNull String accountNumber,
                          @RequestParam("userPassword") @NotEmpty @NotNull String userPassword,
                          HttpServletResponse response) {
        UserModel userModel = userService.userLogin(accountNumber, userPassword);
        System.out.println("登录：" + userModel);
        if (null == userModel) {
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }
        // 处理可能为null的用户状态
        if(userModel.getUserStatus() == null) {
            // 如果状态为null，视为正常用户，设置状态为0
            userModel.setUserStatus((byte) 0);
            // 更新数据库，确保用户状态不为null
            userService.updateUserInfo(userModel);
        } else if(userModel.getUserStatus().equals((byte) 1)){
            return ResultVo.fail(ErrorMsg.ACCOUNT_Ban);
        } else if(userModel.getUserStatus().equals((byte) 2)){
            return ResultVo.fail(ErrorMsg.ACCOUNT_PENDING);
        }
        Cookie cookie = new Cookie("shUserId", String.valueOf(userModel.getId()));
//        cookie.setMaxAge(60 * 60 * 24 * 30);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
        return ResultVo.success(userModel);
    }

    /**
     * 退出登录
     *
     * @param shUserId
     * @param response
     * @return
     */
    @RequestMapping("logout")
    public ResultVo logout(@CookieValue("shUserId")
                           @NotNull(message = "登录异常 请重新登录")
                           @NotEmpty(message = "登录异常 请重新登录") String shUserId, HttpServletResponse response) {
        Cookie cookie = new Cookie("shUserId", shUserId);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResultVo.success();
    }

    /**
     * 获取用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("info")
    public ResultVo getOneUser(@CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
                               @NotEmpty(message = "登录异常 请重新登录")
                                       String id) {
        return ResultVo.success(userService.getUser(Long.valueOf(id)));
    }

    /**
     * 修改用户公开信息
     * @param id
     * @param userModel
     * @return
     */
    @PostMapping("/info")
    public ResultVo updateUserPublicInfo(@CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
                                     @NotEmpty(message = "登录异常 请重新登录")
                                             String id, @RequestBody  UserModel userModel) {
        userModel.setId(Long.valueOf(id));
        
        // 验证银行卡号
        if (userModel.getBankCard() != null && userModel.getBankCard().length() != 16) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("银行卡号必须为16位"));
        }
        // 验证邮箱格式
        if (userModel.getEmail() != null && !userModel.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("邮箱格式不正确"));
        }
        
        if (userService.updateUserInfo(userModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }


    /**
     * 修改密码
     * @param id
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @GetMapping("/password")
    public ResultVo updateUserPassword(@CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
                                       @NotEmpty(message = "登录异常 请重新登录") String id,
                                       @RequestParam("oldPassword") @NotEmpty @NotNull String oldPassword,
                                       @RequestParam("newPassword") @NotEmpty @NotNull String newPassword) {
        if (userService.updatePassword(newPassword,oldPassword,Long.valueOf(id))) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.PASSWORD_RESET_ERROR);
    }
    
    /**
     * 获取账户余额
     * @param id 用户ID
     * @return 账户余额
     */
    @GetMapping("/balance")
    public ResultVo getBalance(@CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
                               @NotEmpty(message = "登录异常 请重新登录") String id) {
        UserModel user = userService.getUser(Long.valueOf(id));
        return ResultVo.success(user.getAccountBalance());
    }

    // 新增：获取用户公开信息接口，用于商品橱窗功能
    /**
     * 获取用户公开信息（供其他用户查看）
     * @param userId 要查看的用户ID
     * @return 用户公开信息
     */
    @GetMapping("/public-info")
    public ResultVo getUserPublicInfo(@RequestParam("userId") @NotNull @NotEmpty Long userId) {
        UserModel user = userService.getUser(userId);
        if (user == null) {
            return ResultVo.fail(ErrorMsg.ACCOUNT_NOT_EXIT);
        }
        
        // 只返回公开信息，隐私信息设为null
        UserModel publicUser = new UserModel();
        publicUser.setId(user.getId());
        publicUser.setNickname(user.getNickname());
        publicUser.setAvatar(user.getAvatar());
        publicUser.setBio(user.getBio());
        publicUser.setSignInTime(user.getSignInTime());
        publicUser.setCity(user.getCity());
        
        return ResultVo.success(publicUser);
    }

    // 新增：用户等级管理接口
    /**
     * 获取用户等级
     * @param id 用户ID
     * @return 用户等级信息
     */
    @GetMapping("/level")
    public ResultVo getUserLevel(@CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
                                @NotEmpty(message = "登录异常 请重新登录") String id) {
        Byte userLevel = userService.getUserLevel(Long.valueOf(id));
        return ResultVo.success(userLevel);
    }
}
