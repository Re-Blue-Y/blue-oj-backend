package com.blue.blueojbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.util.StringUtils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 密钥配置。生产环境必须通过环境变量提供固定密钥。
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class JwtConfig {

    @Bean
    public KeyPair jwtKeyPair(JwtProperties properties, Environment environment) {
        boolean hasPrivateKey = StringUtils.hasText(properties.privateKey());
        boolean hasPublicKey = StringUtils.hasText(properties.publicKey());
        if (hasPrivateKey != hasPublicKey) {
            throw new IllegalStateException("JWT private key and public key must be configured together");
        }
        if (hasPrivateKey) {
            return decodeKeyPair(properties.privateKey(), properties.publicKey());
        }
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            throw new IllegalStateException("JWT_PRIVATE_KEY and JWT_PUBLIC_KEY are required in prod profile");
        }
        log.warn("JWT keys are not configured; generating an ephemeral RSA key pair for non-production use");
        return generateKeyPair();
    }

    private KeyPair decodeKeyPair(String privateKeyValue, String publicKeyValue) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(decodeBase64Key(privateKeyValue))
            );
            PublicKey publicKey = keyFactory.generatePublic(
                    new X509EncodedKeySpec(decodeBase64Key(publicKeyValue))
            );
            RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
            if (!rsaPublicKey.getModulus().equals(rsaPrivateKey.getModulus())) {
                throw new IllegalStateException("JWT private key and public key do not belong to the same key pair");
            }
            return new KeyPair(rsaPublicKey, rsaPrivateKey);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to decode JWT RSA keys", exception);
        }
    }

    private byte[] decodeBase64Key(String value) {
        String normalized = value
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(normalized);
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to generate JWT RSA key pair", exception);
        }
    }
}
