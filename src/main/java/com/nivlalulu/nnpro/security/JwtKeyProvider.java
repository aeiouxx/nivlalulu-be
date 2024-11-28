package com.nivlalulu.nnpro.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtKeyProvider {
    private final SecretKey secretKey;

    public JwtKeyProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
