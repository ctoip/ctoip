package com.aurora.ctoip.security.jwt;

import cn.hutool.core.util.StrUtil;
import com.aurora.ctoip.security.UserDetailsServiceImpl;
import com.aurora.ctoip.service.SysUserService;
import com.aurora.ctoip.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//JWT核心权限处理器
public class JwtAuthCoreFilter extends BasicAuthenticationFilter {

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserDetailsServiceImpl userDetailService;

	@Autowired
	SysUserService sysUserService;

	public JwtAuthCoreFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

		String jwt = request.getHeader(jwtUtils.getHeader());
		if (StrUtil.isBlankOrUndefined(jwt)) {
			chain.doFilter(request, response);
			return;
		}
		Claims claim = jwtUtils.getClaimByToken(jwt);
		if (claim == null) {
			throw new JwtException("token 异常");
		}
		if (jwtUtils.isTokenExpired(claim)) {
			throw new JwtException("token已过期");
		}
		String username = claim.getSubject();
		// 获取用户的权限等信息
//		SysUser sysUser = sysUserService.getByUsername(username);
//		UsernamePasswordAuthenticationToken token
//				= new UsernamePasswordAuthenticationToken(username, null, userDetailService.getUserAuthority(sysUser.getId()));
		//提交权限
		UsernamePasswordAuthenticationToken token
				= new UsernamePasswordAuthenticationToken(username, null, null);
		SecurityContextHolder.getContext().setAuthentication(token);

		chain.doFilter(request, response);
	}
}
