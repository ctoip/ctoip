package com.aurora.ctoip.security.login;

import com.aurora.ctoip.common.lang.Const;
import com.aurora.ctoip.security.CaptchaException;
import com.aurora.ctoip.util.RedisUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author:Aurora
 * @create: 2023-02-21 19:45
 * @Description: 验证码过滤器
 */
@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //判断是否为登录请求
        String url = request.getRequestURI();
        if ("/login".equals(url) && request.getMethod().equals("POST")) {
            try {
                // 校验验证码
                validate(request);
            } catch (CaptchaException e) {
                // 交给认证失败处理器
                loginFailureHandler.onAuthenticationFailure(request, response, e);
            }
        }
        filterChain.doFilter(request, response);
    }

    // 校验验证码逻辑
    private void validate(HttpServletRequest httpServletRequest) {
        //验证码
        String code = httpServletRequest.getParameter("code");
        //key
        String key = httpServletRequest.getParameter("token");

        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
            throw new CaptchaException("验证码错误");
        }

        if (!code.equals(redisUtil.hget(Const.CAPTCHA_KEY, key))) {
            throw new CaptchaException("验证码错误");
        }
        // 一次性使用
        redisUtil.hdel(Const.CAPTCHA_KEY, key);
    }
}
