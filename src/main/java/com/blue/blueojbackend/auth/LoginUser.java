package com.blue.blueojbackend.auth;

import java.time.Instant;

/**
 * 已通过 JWT 校验的当前登录用户。
 */
public record LoginUser(
        Long userId,
        String userRole,
        String tokenId,
        long authVersion,
        Instant expiresAt
) {
}
