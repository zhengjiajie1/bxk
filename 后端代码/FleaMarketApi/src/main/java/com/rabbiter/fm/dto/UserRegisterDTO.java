package com.rabbiter.fm.dto;

import com.rabbiter.fm.model.UserModel;

import java.io.Serializable;

/**
 * 用户注册DTO
 */
public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户信息
     */
    private UserModel userModel;

    /**
     * 验证码ID
     */
    private String captchaId;

    /**
     * 验证码文本
     */
    private String captchaText;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getCaptchaText() {
        return captchaText;
    }

    public void setCaptchaText(String captchaText) {
        this.captchaText = captchaText;
    }
} 