package com.aurora.ctoip.controller;

import cn.hutool.core.map.MapUtil;
import com.aurora.ctoip.common.lang.Result;
import com.aurora.ctoip.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author:Aurora
 * @create: 2023-02-20 18:48
 * @Description:
 */
@RestController
public class TestController {
    @Resource
    SysUserService sysUserService;
    @GetMapping("/test/selectUser")
    public Result selectUser(){
        return Result.success(sysUserService.list());
    }

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @GetMapping("/test/pass")
    public Result passEncode() {
        // 密码加密
        String pass = bCryptPasswordEncoder.encode("111111");
        // 密码验证
        boolean matches = bCryptPasswordEncoder.matches("111111", pass);

        return Result.success(MapUtil.builder()
                .put("pass", pass)
                .put("marches", matches)
                .build()
        );
    }
}
