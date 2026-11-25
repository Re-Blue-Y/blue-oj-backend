package com.blue.blueojbackend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * 已退出 JWT 的 Redis 黑名单。
 */
@Component
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String KEY_PREFIX = "blue-oj:auth:blacklist:";

    private final StringRedisTemplate stringRedisTemplate;

    public boolean contains(String tokenId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key(tokenId)));
    }

    public void add(String tokenId, Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if (!ttl.isNegative() && !ttl.isZero()) {
            stringRedisTemplate.opsForValue().set(key(tokenId), "1", ttl);
        }
    }

    private String key(String tokenId) {
        return KEY_PREFIX + tokenId;
    }
}
