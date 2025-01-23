package com.nivlalulu.nnpro.security.service.impl;

import com.nivlalulu.nnpro.security.service.IRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

//todo: Could use Bucket4j instead of misusing our cache
@Service
@RequiredArgsConstructor
public class RedisRateLimiter implements IRateLimiter {
    private final RedisTemplate<String, Integer> redisTemplate;

    @Override
    public boolean isAllowed(String key, int limit, Duration window) {
        String redisKey = "rate_limit:" + key;
        Integer currentCount = redisTemplate.opsForValue().get(redisKey);

        if (currentCount == null) {
            redisTemplate.opsForValue().set(redisKey, 1, window);
            return true;
        }

        if (currentCount >= limit) {
            return false; // Rate limit exceeded
        }

        redisTemplate.opsForValue().increment(redisKey);
        return true;
    }
}
