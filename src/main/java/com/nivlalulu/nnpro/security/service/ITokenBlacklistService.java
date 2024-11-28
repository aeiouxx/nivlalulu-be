package com.nivlalulu.nnpro.security.service;

import java.time.Duration;

public interface ITokenBlacklistService {
    void blacklist(String jti, Duration expiration);
    boolean isBlacklisted(String jti);
}
