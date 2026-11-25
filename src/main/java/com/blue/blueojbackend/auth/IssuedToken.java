package com.blue.blueojbackend.auth;

import java.time.Instant;

/**
 * 新签发的访问令牌。
 */
public record IssuedToken(String value, Instant expiresAt) {
}
