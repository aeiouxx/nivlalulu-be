package com.nivlalulu.nnpro.security.service.impl;

import com.nivlalulu.nnpro.security.service.ITokenCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Deprecated
@Service
@RequiredArgsConstructor
public class RedisTokenCacheService implements ITokenCacheService {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void add(String jti, Duration expiration) {
        redisTemplate.opsForValue().set(jti, jti, expiration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isPresent(String jti) {
        return redisTemplate.hasKey(jti);
    }
}
