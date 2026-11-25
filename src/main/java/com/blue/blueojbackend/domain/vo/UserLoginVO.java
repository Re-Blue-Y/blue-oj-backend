package com.blue.blueojbackend.domain.vo;

import java.time.Instant;

/**
 * 登录成功响应。
 */
public record UserLoginVO(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        UserVO user
) {
}
