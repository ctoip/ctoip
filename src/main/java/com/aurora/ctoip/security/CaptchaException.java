package com.aurora.ctoip.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @author:Aurora
 * @create: 2023-02-21 23:06
 * @Description: 验证码异常
 */
public class CaptchaException extends AuthenticationException {
    public CaptchaException(String msg) {
        super(msg);
    }
}
