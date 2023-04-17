package com.aurora.ctoip.security;

import com.aurora.ctoip.common.exception.GlobalExceptionHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author:Aurora
 * @create: 2023-04-17 09:49
 * @Description: 自定义csrf返回
 */
public class MyCsrfFilter extends OncePerRequestFilter {

    public static final RequestMatcher DEFAULT_CSRF_MATCHER = new MyCsrfFilter.DefaultRequiresCsrfMatcher();
    private static final String SHOULD_NOT_FILTER = "SHOULD_NOT_FILTER" + MyCsrfFilter.class.getName();
    private final Log logger = LogFactory.getLog(getClass());
    private final CsrfTokenRepository tokenRepository;
    @Autowired
    GlobalExceptionHandler exceptionHandler;
    private RequestMatcher requireCsrfProtectionMatcher = DEFAULT_CSRF_MATCHER;

    private AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();

    public MyCsrfFilter(CsrfTokenRepository csrfTokenRepository) {
        Assert.notNull(csrfTokenRepository, "csrfTokenRepository cannot be null");
        this.tokenRepository = csrfTokenRepository;
    }

    public static void skipRequest(HttpServletRequest request) {
        request.setAttribute(SHOULD_NOT_FILTER, Boolean.TRUE);
    }


    private static boolean equalsConstantTime(String expected, String actual) {
        if (expected == actual) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        // Encode after ensure that the string is not null
        byte[] expectedBytes = Utf8.encode(expected);
        byte[] actualBytes = Utf8.encode(actual);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Boolean.TRUE.equals(request.getAttribute(SHOULD_NOT_FILTER));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        request.setAttribute(HttpServletResponse.class.getName(), response);
        CsrfToken csrfToken = this.tokenRepository.loadToken(request);
        boolean missingToken = (csrfToken == null);
        if (missingToken) {
            csrfToken = this.tokenRepository.generateToken(request);
            this.tokenRepository.saveToken(csrfToken, request, response);
        }
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        request.setAttribute(csrfToken.getParameterName(), csrfToken);
        if (!this.requireCsrfProtectionMatcher.matches(request)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Did not protect against CSRF since request did not match "
                        + this.requireCsrfProtectionMatcher);
            }
            filterChain.doFilter(request, response);
            return;
        }
        String actualToken = request.getHeader(csrfToken.getHeaderName());
        if (actualToken == null) {
            actualToken = request.getParameter(csrfToken.getParameterName());
        }
        if (!equalsConstantTime(csrfToken.getToken(), actualToken)) {
            this.logger.debug(
                    LogMessage.of(() -> "Invalid CSRF token found for " + UrlUtils.buildFullRequestUrl(request)));
            AccessDeniedException exception = new AccessDeniedException("CSRFtoken错误");
            exceptionHandler.handler(exception);
            return;
        }
        filterChain.doFilter(request, response);
    }


    public void setRequireCsrfProtectionMatcher(RequestMatcher requireCsrfProtectionMatcher) {
        Assert.notNull(requireCsrfProtectionMatcher, "requireCsrfProtectionMatcher cannot be null");
        this.requireCsrfProtectionMatcher = requireCsrfProtectionMatcher;
    }


    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        Assert.notNull(accessDeniedHandler, "accessDeniedHandler cannot be null");
        this.accessDeniedHandler = accessDeniedHandler;
    }

    private static final class DefaultRequiresCsrfMatcher implements RequestMatcher {

        private final HashSet<String> allowedMethods = new HashSet<>(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));

        @Override
        public boolean matches(HttpServletRequest request) {
            return !this.allowedMethods.contains(request.getMethod());
        }

        @Override
        public String toString() {
            return "CsrfNotRequired " + this.allowedMethods;
        }

    }

}
