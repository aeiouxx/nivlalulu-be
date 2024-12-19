package com.nivlalulu.nnpro.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {
    public static final String NOTIFICATION_EXECUTOR_KEY = "notification_executor";


    // the numbers here are very arbitrary, sending emails synchronously would be good enough for this project anyway
    @Bean(name = NOTIFICATION_EXECUTOR_KEY)
    public ThreadPoolTaskExecutor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(5);
        executor.setThreadNamePrefix(NOTIFICATION_EXECUTOR_KEY + "-");
        executor.initialize();
        return executor;
    }
}
