package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.dto.v1.AuthenticationResponseDto;
import com.nivlalulu.nnpro.dto.v1.LoginRequestDto;
import com.nivlalulu.nnpro.dto.v1.RefreshTokenResponseDto;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IRefreshTokenRepository;
import com.nivlalulu.nnpro.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class JwtTokenIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IRefreshTokenRepository refreshTokenRepository;

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass")
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void registerDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        var user = new User();
        user.setUsername("testuser2");
        user.setEmail("testuser2@example.com");
        user.setPassword(passwordEncoder.encode("testpass2"));
        userRepository.save(user);
    }

    @Test
    void testRefreshTokenSuccess() {
        // Assume we have a way to get a valid refresh token (e.g., from login)
        var loginRequest = new LoginRequestDto("testuser2", "testpass2");
        var loginResponse = restTemplate.postForEntity("/public/v1/auth/login",
                loginRequest,
                AuthenticationResponseDto.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        var cookies = loginResponse.getHeaders().get("Set-Cookie");
        assertTrue(cookies.stream().anyMatch(c -> c.contains("HttpOnly")));

        // Now call /refresh endpoint
        // Since refresh uses cookie, just send the same cookie back
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookies.get(0));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        var refreshResponse = restTemplate.exchange("/public/v1/token/refresh",
                HttpMethod.POST,
                requestEntity,
                RefreshTokenResponseDto.class);
        assertEquals(HttpStatus.OK, refreshResponse.getStatusCode());
        assertNotNull(refreshResponse.getBody().accessToken());
    }

    @Test
    void testRefreshTokenInvalid() {
        // No valid cookie
        var refreshResponse = restTemplate.postForEntity("/public/v1/token/refresh",
                null,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, refreshResponse.getStatusCode());
    }
}
