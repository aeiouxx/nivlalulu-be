package com.nivlalulu.nnpro.security.service;

import java.time.Duration;

@Deprecated
public interface ITokenBlacklistService {
    void blacklist(String jti, Duration expiration);
    boolean isBlacklisted(String jti);
}
