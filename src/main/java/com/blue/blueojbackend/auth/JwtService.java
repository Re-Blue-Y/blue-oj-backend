package com.blue.blueojbackend.auth;

import com.blue.blueojbackend.common.ErrorCode;
import com.blue.blueojbackend.config.JwtProperties;
import com.blue.blueojbackend.exception.BusinessException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * RS256 JWT 签发与校验组件。
 */
@Component
public class JwtService {

    private static final String ROLE_CLAIM = "role";
    private static final String AUTH_VERSION_CLAIM = "ver";

    private final JwtProperties properties;
    private final RSASSASigner signer;
    private final RSASSAVerifier verifier;

    public JwtService(JwtProperties properties, KeyPair jwtKeyPair) {
        this.properties = properties;
        this.signer = new RSASSASigner((RSAPrivateKey) jwtKeyPair.getPrivate());
        this.verifier = new RSASSAVerifier((RSAPublicKey) jwtKeyPair.getPublic());
    }

    public IssuedToken issue(Long userId, String userRole, long authVersion) {
        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiresAt = issuedAt.plus(properties.accessTokenTtl());
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(properties.issuer())
                .audience(properties.audience())
                .subject(String.valueOf(userId))
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiresAt))
                .jwtID(UUID.randomUUID().toString())
                .claim(ROLE_CLAIM, userRole)
                .claim(AUTH_VERSION_CLAIM, authVersion)
                .build();
        SignedJWT signedJwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .type(JOSEObjectType.JWT)
                        .build(),
                claims
        );
        try {
            signedJwt.sign(signer);
            return new IssuedToken(signedJwt.serialize(), expiresAt);
        } catch (JOSEException exception) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录凭证签发失败");
        }
    }

    public LoginUser parseAndValidate(String token) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            validateHeaderAndSignature(signedJwt);
            return validateClaims(signedJwt.getJWTClaimsSet());
        } catch (ParseException | JOSEException | IllegalArgumentException exception) {
            throw invalidToken();
        }
    }

    private void validateHeaderAndSignature(SignedJWT signedJwt) throws JOSEException {
        if (!JWSAlgorithm.RS256.equals(signedJwt.getHeader().getAlgorithm())
                || !JOSEObjectType.JWT.equals(signedJwt.getHeader().getType())
                || !signedJwt.verify(verifier)) {
            throw invalidToken();
        }
    }

    private LoginUser validateClaims(JWTClaimsSet claims) throws ParseException {
        Instant now = Instant.now();
        Instant clockSkewBefore = now.minus(properties.clockSkew());
        Instant clockSkewAfter = now.plus(properties.clockSkew());
        Date expirationTime = claims.getExpirationTime();
        Date issuedAt = claims.getIssueTime();
        List<String> audience = claims.getAudience();
        if (!Objects.equals(properties.issuer(), claims.getIssuer())
                || audience == null
                || !audience.contains(properties.audience())
                || expirationTime == null
                || expirationTime.toInstant().isBefore(clockSkewBefore)
                || issuedAt == null
                || issuedAt.toInstant().isAfter(clockSkewAfter)
                || claims.getSubject() == null
                || claims.getJWTID() == null) {
            throw invalidToken();
        }
        Long userId = Long.valueOf(claims.getSubject());
        String userRole = claims.getStringClaim(ROLE_CLAIM);
        Object authVersionClaim = claims.getClaim(AUTH_VERSION_CLAIM);
        Number authVersion = authVersionClaim instanceof Number number ? number : null;
        if (userRole == null || authVersion == null) {
            throw invalidToken();
        }
        return new LoginUser(
                userId,
                userRole,
                claims.getJWTID(),
                authVersion.longValue(),
                expirationTime.toInstant()
        );
    }

    private BusinessException invalidToken() {
        return new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "登录凭证无效或已过期");
    }
}
