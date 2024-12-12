package com.nivlalulu.nnpro.example;

import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.security.JwtTokenProvider;
import com.nivlalulu.nnpro.security.service.impl.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ExampleIntegrationTest {
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    @Autowired
    private  JwtTokenProvider jwtTokenProvider;
    @Autowired
    private  IUserRepository userRepository;
    @Autowired
    private  UserDetailsService userDetailsService;


    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass")
            .waitingFor(Wait.forListeningPort());

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());


    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    public void testBlacklist() {
        var user = new User();
        user.setUsername("test");
        user.setEmail("test@test.cz");
        user.setPassword("test");
        user = userRepository.save(user);
    }
}
