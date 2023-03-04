package com.aurora.ctoip.config;

import com.aurora.ctoip.security.*;
import com.aurora.ctoip.security.jwt.JwtAccessDeniedHandler;
import com.aurora.ctoip.security.jwt.JwtAuthCoreFilter;
import com.aurora.ctoip.security.jwt.JwtAuthEntryPoint;
import com.aurora.ctoip.security.jwt.JwtLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 安全拦截处理
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	LoginFailureHandler loginFailureHandler;

	@Autowired
	LoginSuccessHandler loginSuccessHandler;

	@Autowired
	CaptchaFilter captchaFilter;

	@Autowired
	JwtAuthEntryPoint jwtAuthEntryPoint;

	@Autowired
	JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Autowired
	JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	//从数据库中取得密码
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}
	//使用核心权限处理器
	@Bean
	JwtAuthCoreFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthCoreFilter filter = new JwtAuthCoreFilter(authenticationManager());
		return filter;
	}
	//使用BC加密的密文
	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	//配置白名单
	private static final String[] URL_WHITELIST = {
			"/login",
			"/logout",
			"/captcha",
			"/favicon.ico",
			"/test/**",
			"/IpTrace/**"
	};

	//设置SpringSecurity处理的内容
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
				// 登录配置
				.formLogin()
				.successHandler(loginSuccessHandler)
				.failureHandler(loginFailureHandler)
				.and()
				.logout()
				.logoutSuccessHandler(jwtLogoutSuccessHandler)
				// 禁用session
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				// 配置拦截规则
				.and()
				.authorizeRequests()
				.antMatchers(URL_WHITELIST).permitAll()
				.anyRequest().authenticated()
				// 异常处理器
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler)
				// 配置自定义的过滤器
				.and()
				.addFilter(jwtAuthenticationFilter())
				.addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
		;
	}
}
