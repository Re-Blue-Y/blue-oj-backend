package com.blue.blueojbackend.interceptor;

import com.blue.blueojbackend.auth.AuthVersionService;
import com.blue.blueojbackend.auth.JwtService;
import com.blue.blueojbackend.auth.LoginUser;
import com.blue.blueojbackend.auth.TokenBlacklistService;
import com.blue.blueojbackend.auth.UserContext;
import com.blue.blueojbackend.common.ErrorCode;
import com.blue.blueojbackend.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Bearer JWT 登录拦截器。
 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int MAX_TOKEN_LENGTH = 8192;

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthVersionService authVersionService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        UserContext.remove();
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String token = resolveToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        LoginUser loginUser = jwtService.parseAndValidate(token);
        if (tokenBlacklistService.contains(loginUser.tokenId())) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "登录凭证已失效");
        }
        long currentVersion = authVersionService.getVersion(loginUser.userId());
        if (currentVersion != loginUser.authVersion()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "登录状态已失效，请重新登录");
        }
        UserContext.set(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception
    ) {
        UserContext.remove();
    }

    private String resolveToken(String authorization) {
        if (!StringUtils.hasText(authorization)
                || authorization.length() <= BEARER_PREFIX.length()
                || !authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请提供 Bearer 登录凭证");
        }
        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty() || token.length() > MAX_TOKEN_LENGTH) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "登录凭证格式错误");
        }
        return token;
    }
}
