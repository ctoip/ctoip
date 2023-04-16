package com.aurora.ctoip.config;

import com.aurora.ctoip.security.ExceptionAccessDeniedHandler;
import com.aurora.ctoip.security.FailureAuthEntryPoint;
import com.aurora.ctoip.security.MyCsrfTokenRepository;
import com.aurora.ctoip.security.NomalAuthCoreFilter;
import com.aurora.ctoip.security.login.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * @author Aurora
 * @Description:核心配置
 * @since 2023-02-21 01:33
 */
@Configuration
@EnableWebSecurity
//权限验证注解
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //配置白名单
    private static final String[] URL_WHITELIST = {
            "/login",
            "/logout",
            "/captcha",
            "/favicon.ico",
            "/test/**",
    };
    @Autowired
    LoginFailureHandler loginFailureHandler;
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    CaptchaFilter captchaFilter;
    @Autowired
    FailureAuthEntryPoint failureAuthEntryPoint;
    @Autowired
    ExceptionAccessDeniedHandler exceptionAccessDeniedHandler;
    @Autowired
    LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    //用户密码加密方式
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //从数据库中取得密码
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    //声明核心权限处理器
    @Bean
    NomalAuthCoreFilter nomalAuthenticationFilter() throws Exception {
        NomalAuthCoreFilter filter = new NomalAuthCoreFilter(authenticationManager());
        return filter;
    }

    //使用自己配置的配置CSRF
    @Bean
    @ConditionalOnMissingBean
    public MyCsrfTokenRepository csrfTokenRepository() {
        MyCsrfTokenRepository repository = new MyCsrfTokenRepository();
        //关闭Httponly,让前端可以js访问cookie
        repository.setCookieHttpOnly(false);
        //請求頭名稱
        repository.setHeaderName("setHeaderName");
        //Cookie的key名
        repository.setCookieName("setCookieName");
        repository.setCookieMaxAge(6000 * 60 * 60);
        return repository;
    }


    //设置SpringSecurity处理的内容
    protected void configure(HttpSecurity http) throws Exception {
        http.headers()
                .xssProtection()
                .xssProtectionEnabled(true)
                .block(true);
        http.cors().and()
                .csrf()
                .csrfTokenRepository(csrfTokenRepository())
                .and()
                // 登录配置
                .formLogin()
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler)
                // HttpSecurity禁用session
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // HttpSecurity配置拦截规则
                .and()
                .authorizeRequests()
                .antMatchers(URL_WHITELIST).permitAll()
                .anyRequest().authenticated()
                // 异常处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(failureAuthEntryPoint)
                .accessDeniedHandler(exceptionAccessDeniedHandler)
                // 配置自定义的过滤器,BasicAuthenticationFilter
                .and()
                .addFilter(nomalAuthenticationFilter())
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class) //前置过滤器
        ;

    }
}
