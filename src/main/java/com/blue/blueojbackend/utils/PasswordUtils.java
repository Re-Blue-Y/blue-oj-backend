package com.blue.blueojbackend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 密码哈希与校验工具。
 *
 * <p>密码只能进行单向哈希，登录时通过 matches 比对，不能解密。</p>
 */
@Component
@RequiredArgsConstructor
public class PasswordUtils {

    private static final int BCRYPT_MAX_PASSWORD_BYTES = 72;

    private final PasswordEncoder passwordEncoder;

    public String encode(String rawPassword) {
        if (!isEncodingSupported(rawPassword)) {
            throw new IllegalArgumentException("BCrypt password must not exceed 72 bytes");
        }
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (!isEncodingSupported(rawPassword) || encodedPassword == null || encodedPassword.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public boolean isEncodingSupported(String rawPassword) {
        return rawPassword != null
                && rawPassword.getBytes(StandardCharsets.UTF_8).length <= BCRYPT_MAX_PASSWORD_BYTES;
    }
}
