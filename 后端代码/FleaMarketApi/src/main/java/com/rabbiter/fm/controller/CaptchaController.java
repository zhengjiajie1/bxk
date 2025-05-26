package com.rabbiter.fm.controller;

import com.rabbiter.fm.dto.CaptchaDTO;
import com.rabbiter.fm.service.CaptchaService;
import com.rabbiter.fm.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    /**
     * 生成验证码
     * @return 验证码图片和ID
     */
    @GetMapping("/generate")
    public ResultVo generateCaptcha() {
        CaptchaDTO captchaDTO = captchaService.generateCaptcha();
        return ResultVo.success(captchaDTO);
    }
} 