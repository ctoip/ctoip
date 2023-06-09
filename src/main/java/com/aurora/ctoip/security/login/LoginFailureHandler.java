package com.aurora.ctoip.security.login;

import cn.hutool.json.JSONUtil;
import com.aurora.ctoip.common.lang.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author:Aurora
 * @create: 2023-02-21 19:22
 * @Description: 登录失败处理器, 给前端返回信息
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        //resp流
        ServletOutputStream outputStream = response.getOutputStream();
        Result result = Result.fail(
                "Bad credentials".equals(exception.getMessage()) ? "用户名或密码不正确" : exception.getMessage()
        );
        outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }
}
