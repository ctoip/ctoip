package com.aurora.ctoip.controller;

import cn.hutool.http.server.HttpServerRequest;
import com.aurora.ctoip.service.SysUserService;
import com.aurora.ctoip.util.RedisUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * @author:Aurora
 * @create: 2023-02-21 00:24
 * @Description: 通用控制器
 */
public class BaseController {
    @Resource
    HttpServletRequest request;

    @Resource
    RedisUtil redisUtil;

    @Resource
    SysUserService sysUserService;

}
