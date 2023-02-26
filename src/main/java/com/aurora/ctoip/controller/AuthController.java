package com.aurora.ctoip.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import com.aurora.ctoip.common.lang.Const;
import com.aurora.ctoip.common.lang.Result;
import com.google.code.kaptcha.Producer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author:Aurora
 * @create: 2023-02-21 00:17
 * @Description: 核心登录验证
 */
@RestController
public class AuthController extends BaseController{

    @Resource
    Producer producer;

    @GetMapping("/captcha")
    public Result Captcha() throws IOException {

        //key作为redis的唯一标识, redis充当的是一个cookie的身份
        String key = UUID.randomUUID().toString();
        //用于根据KaptchaConfig生成随机字符
        String code = producer.createText();

        key = "aaaaa";
        code = "11111";
        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream out = new ByteArrayOutputStream();//get ByteImage
        ImageIO.write(image,"png",out);
        BASE64Encoder encoder = new BASE64Encoder();
        String str = "data:image/png;base64,";//image header
        String base64img = str + encoder.encode(out.toByteArray());
        redisUtil.hset(Const.CAPTCHA_KEY,key,code);
        return Result.success(
                MapUtil.builder()
                        //token指key
                        .put("token",key)
                        .put("base64img",base64img).build()
        );
    }
}
