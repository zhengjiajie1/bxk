package com.rabbiter.fm.service.impl;

import com.rabbiter.fm.common.utils.CaptchaUtil;
import com.rabbiter.fm.dto.CaptchaDTO;
import com.rabbiter.fm.service.CaptchaService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务实现类
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    /**
     * 验证码有效期（分钟）
     */
    private static final long CAPTCHA_EXPIRATION_MINUTES = 5;
    
    /**
     * 验证码长度
     */
    private static final int CAPTCHA_LENGTH = 4;
    
    /**
     * 使用内存Map存储验证码，替代Redis
     */
    private final Map<String, String> captchaStore = new ConcurrentHashMap<>();

    @Override
    public CaptchaDTO generateCaptcha() {
        // 生成验证码文本
        String captchaText = CaptchaUtil.generateCaptchaText(CAPTCHA_LENGTH);
        
        // 生成验证码ID
        String captchaId = UUID.randomUUID().toString();
        
        // 存储验证码到内存Map
        captchaStore.put(captchaId, captchaText);
        
        // 设置定时清除过期验证码
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                captchaStore.remove(captchaId);
            }
        }, CAPTCHA_EXPIRATION_MINUTES * 60 * 1000);
        
        // 生成验证码图片
        String captchaImage = CaptchaUtil.generateCaptchaImage(captchaText);
        
        // 创建并返回验证码DTO
        CaptchaDTO captchaDTO = new CaptchaDTO();
        captchaDTO.setCaptchaId(captchaId);
        captchaDTO.setCaptchaImage(captchaImage);
        
        return captchaDTO;
    }

    @Override
    public boolean validateCaptcha(String captchaId, String captchaText) {
        if (captchaId == null || captchaText == null) {
            return false;
        }
        
        // 从内存Map获取验证码并立即删除
        String storedCaptcha = captchaStore.remove(captchaId);
        
        if (storedCaptcha == null) {
            return false;
        }
        
        // 比较验证码（忽略大小写）
        return captchaText.equalsIgnoreCase(storedCaptcha);
    }
} 