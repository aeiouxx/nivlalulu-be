package com.nivlalulu.nnpro.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtKeyProvider {
    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    public JwtKeyProvider(
            @Value("${jwt.access-secret}") String accessSecret,
            @Value("${jwt.refresh-secret}") String refreshSecret) {
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes());
    }

    public SecretKey getKey(JwtTokenType type) {
        switch (type) {
            case ACCESS:
                return accessKey;
            case REFRESH:
                return refreshKey;
            default:
                throw new IllegalArgumentException("Unknown token type: " + type);
        }
    }
}
