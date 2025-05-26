package com.rabbiter.fm.dto;

import java.io.Serializable;

/**
 * 验证码传输对象
 */
public class CaptchaDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 验证码唯一标识，用于验证时找到对应的验证码
     */
    private String captchaId;
    
    /**
     * 验证码图片（Base64编码）
     */
    private String captchaImage;

    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getCaptchaImage() {
        return captchaImage;
    }

    public void setCaptchaImage(String captchaImage) {
        this.captchaImage = captchaImage;
    }
} 