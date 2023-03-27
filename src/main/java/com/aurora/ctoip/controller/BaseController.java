package com.aurora.ctoip.controller;

import com.aurora.ctoip.service.SysUserService;
import com.aurora.ctoip.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author:Aurora
 * @create: 2023-02-21 00:24
 * @Description: 通用控制器
 */
@Data
@Component
@ConfigurationProperties(prefix = "aurora.apikey")
public class BaseController {
    public String threatBookApiKey;
    public String VTApiKey;
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
    @Resource
    ObjectMapper objectMapper;

}
