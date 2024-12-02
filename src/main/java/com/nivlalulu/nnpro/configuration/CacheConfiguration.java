package com.nivlalulu.nnpro.configuration;

import com.nivlalulu.nnpro.security.JwtTokenProvider;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

@Configuration(proxyBeanMethods = false)
public class CacheConfiguration {
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration("jwt-blacklist",
                        RedisCacheConfiguration
                                .defaultCacheConfig()
                                .entryTtl(JwtTokenProvider.EXPIRATION_TIME) );
    }
}
