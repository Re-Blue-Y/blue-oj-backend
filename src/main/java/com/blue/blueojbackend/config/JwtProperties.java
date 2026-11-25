package com.blue.blueojbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * JWT 签发与校验配置。
 *
 * @param issuer 令牌签发方
 * @param audience 令牌目标服务
 * @param accessTokenTtl 访问令牌有效期
 * @param clockSkew 允许的服务器时钟偏差
 * @param privateKey Base64 编码的 PKCS#8 RSA 私钥
 * @param publicKey Base64 编码的 X.509 RSA 公钥
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String issuer,
        String audience,
        Duration accessTokenTtl,
        Duration clockSkew,
        String privateKey,
        String publicKey
) {
    public JwtProperties {
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("app.jwt.issuer must not be blank");
        }
        if (audience == null || audience.isBlank()) {
            throw new IllegalArgumentException("app.jwt.audience must not be blank");
        }
        if (accessTokenTtl == null || accessTokenTtl.isZero() || accessTokenTtl.isNegative()) {
            throw new IllegalArgumentException("app.jwt.access-token-ttl must be positive");
        }
        if (clockSkew == null || clockSkew.isNegative()) {
            throw new IllegalArgumentException("app.jwt.clock-skew must not be negative");
        }
    }
}
