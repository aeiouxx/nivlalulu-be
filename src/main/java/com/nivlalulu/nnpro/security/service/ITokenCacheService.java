package com.nivlalulu.nnpro.security.service;

import java.time.Duration;

@Deprecated
public interface ITokenCacheService {
    void add(String jti, Duration expiration);
    boolean isPresent(String jti);
}
