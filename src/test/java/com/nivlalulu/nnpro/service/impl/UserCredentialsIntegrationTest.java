package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.email.IMailSender;
import com.nivlalulu.nnpro.common.hashing.IHashProvider;
import com.nivlalulu.nnpro.dto.v1.CreatePasswordResetTokenDto;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IPasswordResetTokenRepository;
import com.nivlalulu.nnpro.repository.IUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserCredentialsIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IPasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private IMailSender mailSender;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass")
            .waitingFor(Wait.forListeningPort());

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void postgresProps(DynamicPropertyRegistry registry) {
        // PostgreSQL properties
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Redis properties
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        passwordResetTokenRepository.deleteAll();
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("oldPass"));
        userRepository.save(user);

        doNothing().when(mailSender)
                .sendResetCode(anyString(), anyString());
    }

    @Test
    void testRequestPasswordResetToken() {
        CreatePasswordResetTokenDto dto = new CreatePasswordResetTokenDto("testuser");
        ResponseEntity<Void> response = restTemplate.postForEntity("/public/v1/password-reset/request",
                dto,
                Void.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Actual response: " + response);
        Assertions.assertFalse(passwordResetTokenRepository.findAll().isEmpty());
    }

    @Test
    void testRequestPasswordResetTokenUserNotFound() {
        CreatePasswordResetTokenDto dto = new CreatePasswordResetTokenDto("unknown");

        ResponseEntity<Void> response = restTemplate.postForEntity("/public/v1/password-reset/request",
                dto,
                Void.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
