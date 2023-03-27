package com.aurora.ctoip.common.filter;

import com.aurora.ctoip.common.Wapper.RequestWrapper;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @Author
 * @Date
 * @Description sql防注入过滤器, 只对请求过滤了，未对Header进行过滤
 */
@WebFilter(urlPatterns = "/*", filterName = "MySqXssFilter")
@Configuration
public class SqlXssInjectionFilter implements Filter {

    // 效验
    protected static boolean sqlValidate(String str) {
        String s = str.toLowerCase();// 统一转为小写
        String badStr = "select|update|and|or|delete|insert|truncate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute|table|"
                + "char|declare|sitename|xp_cmdshell|like|from|grant|use|group_concat|column_name|"
                + "information_schema.columns|table_schema|union|where|order|by|"
                + "script|img|src|"
                + "'\\*|\\;|\\-|\\--|\\+|\\,|\\//|\\/|\\%|\\#";// 过滤掉的sql关键字，特殊字符前面需要加\\进行转义
        // 使用正则表达式进行匹配
        boolean matches = s.matches(badStr);
        return matches;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String method = request.getMethod();
        if ("post".equalsIgnoreCase(method)) {
            //POST
            //处理请求体：'Content-Type': "application/json; charset=utf-8"
            //必须使用流式读取，而不能使用request.getParameterNames();读取因为请求体不再是Key-Value格式
            RequestWrapper requestWrapper = new RequestWrapper(request);
            String body = requestWrapper.getBody();
            if (sqlValidate(body)) {
                throw new RuntimeException("非法字符");
            } else {
                filterChain.doFilter(requestWrapper, response);
            }
        } else {
            //GET
            Enumeration<String> names = request.getParameterNames();
            String sql = "";
            while (names.hasMoreElements()) {
                String name = names.nextElement().toString();
                String[] values = request.getParameterValues(name);
                for (int i = 0; i < values.length; i++) {
                    sql += values[i];
                }
            }
            if (sqlValidate(sql)) {
                throw new RuntimeException("非法字符");
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
