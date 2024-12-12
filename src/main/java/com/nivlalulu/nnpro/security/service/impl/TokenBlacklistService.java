package com.nivlalulu.nnpro.security.service.impl;

import com.nivlalulu.nnpro.security.service.ITokenBlacklistService;
import com.nivlalulu.nnpro.security.service.ITokenCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Deprecated
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService implements ITokenBlacklistService {
    private final ITokenCacheService tokenCacheService;

    @Override
    public void blacklist(String jti, Duration expiration) {
        log.debug("Blacklisting token with jti: {}", jti);
        tokenCacheService.add(jti, expiration);
    }

    @Override
    public boolean isBlacklisted(String jti) {
        var blacklisted = tokenCacheService.isPresent(jti);
        log.debug("Token with jti: {} is blacklisted: {}", jti, blacklisted);
        return blacklisted;
    }
}
