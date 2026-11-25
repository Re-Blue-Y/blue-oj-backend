package com.blue.blueojbackend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 用户认证版本。修改密码、角色或封禁状态时递增，可使旧 JWT 立即失效。
 */
@Component
@RequiredArgsConstructor
public class AuthVersionService {

    private static final String KEY_PREFIX = "blue-oj:auth:version:";

    private final StringRedisTemplate stringRedisTemplate;

    public long getVersion(Long userId) {
        String value = stringRedisTemplate.opsForValue().get(key(userId));
        return value == null ? 0L : Long.parseLong(value);
    }

    public long incrementVersion(Long userId) {
        Long version = stringRedisTemplate.opsForValue().increment(key(userId));
        return version == null ? 0L : version;
    }

    private String key(Long userId) {
        return KEY_PREFIX + userId;
    }
}
