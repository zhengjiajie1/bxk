package com.rabbiter.fm.service;

import com.rabbiter.fm.dto.CaptchaDTO;

/**
 * 验证码服务接口
 */
public interface CaptchaService {

    /**
     * 生成验证码
     * @return 验证码数据传输对象
     */
    CaptchaDTO generateCaptcha();

    /**
     * 验证验证码
     * @param captchaId 验证码ID
     * @param captchaText 用户输入的验证码
     * @return 验证结果
     */
    boolean validateCaptcha(String captchaId, String captchaText);
} 