package com.aurora.ctoip.controller;

import cn.hutool.Hutool;
import cn.hutool.http.server.HttpServerRequest;
import com.aurora.ctoip.service.SysUserService;
import com.aurora.ctoip.util.RedisUtil;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author:Aurora
 * @create: 2023-02-21 00:24
 * @Description: 通用控制器
 */
public class BaseController {
    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    @Resource
    RestTemplate restTemplate;

    @Resource
    RedisUtil redisUtil;

    @Resource
    SysUserService sysUserService;


}
