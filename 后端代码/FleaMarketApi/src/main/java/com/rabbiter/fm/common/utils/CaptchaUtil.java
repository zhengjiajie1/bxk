package com.rabbiter.fm.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码工具类
 */
public class CaptchaUtil {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;

    /**
     * 生成随机验证码
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateCaptchaText(int length) {
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < length; i++) {
            captcha.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return captcha.toString();
    }

    /**
     * 根据验证码文本生成验证码图片
     * @param captchaText 验证码文本
     * @return Base64编码的图片
     */
    public static String generateCaptchaImage(String captchaText) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        // 添加干扰线
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            g.drawLine(x1, y1, x2, y2);
        }
        
        // 添加噪点
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            g.drawOval(x, y, 1, 1);
        }
        
        // 绘制验证码
        for (int i = 0; i < captchaText.length(); i++) {
            float x = 20 + i * 25 + random.nextFloat() * 5;
            float y = 20 + random.nextFloat() * 5;
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            // 随机旋转
            double degree = random.nextInt(30) * Math.PI / 180;
            g.rotate(degree, x, y);
            g.drawString(String.valueOf(captchaText.charAt(i)), x, y);
            g.rotate(-degree, x, y);
        }
        
        g.dispose();
        
        // 转为Base64
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 