package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.UnauthorizedException;
import com.nivlalulu.nnpro.dto.v1.LoginRequestDto;
import com.nivlalulu.nnpro.dto.v1.TokenDto;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private HttpServletResponse httpResponse;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginWithValidCredentials_ShouldSucceed() {
        var user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPass");
        var accessTokenDto = new TokenDto("accessToken", null, null, 0);

        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("rawpass", "encodedPass")).thenReturn(true);

        when(jwtTokenService.generateNewRefreshToken(user)).thenReturn("refreshToken");
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(accessTokenDto);

        var request = new LoginRequestDto("testuser", "rawpass");
        var responseDto = authService.login(request, httpResponse);

        assertEquals(accessTokenDto.content(), responseDto.accessToken().content());
        assertEquals("testuser", responseDto.username());
        verify(jwtTokenProvider).attachRefreshTokenToCookie(httpResponse, "refreshToken");
    }

    @Test
    void loginWithInvalidCredentials_ShouldThrow() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(java.util.Optional.empty());

        var request = new LoginRequestDto("testuser", "rawpass");
        assertThrows(UnauthorizedException.class, () -> authService.login(request, httpResponse));
    }
}
