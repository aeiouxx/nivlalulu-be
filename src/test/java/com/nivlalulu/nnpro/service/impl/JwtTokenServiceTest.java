package com.nivlalulu.nnpro.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nivlalulu.nnpro.common.exceptions.InvalidTokenException;
import com.nivlalulu.nnpro.model.RefreshToken;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IRefreshTokenRepository;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.security.JwtTokenProvider;
import com.nivlalulu.nnpro.security.JwtTokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.Instant;
import java.util.Optional;

class JwtTokenServiceTest {
    @Mock
    private IRefreshTokenRepository refreshTokenRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("encoded");
    }

    @Test
    // I hate mocking so much
    void refreshAndRotate_ShouldSucceed() {
        var refreshToken = new RefreshToken("tokenId", Instant.now().plusSeconds(3600), user);
        when(jwtTokenProvider.extractRefreshTokenFromCookie(request))
                .thenReturn("refreshToken");
        when(jwtTokenProvider.extractRefreshTokenId("refreshToken"))
                .thenReturn("tokenId");
        when(jwtTokenProvider.extractUsername("refreshToken", JwtTokenType.REFRESH))
                .thenReturn("testuser");
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));
        when(refreshTokenRepository.findByTokenId("tokenId"))
                .thenReturn(Optional.of(refreshToken));

        when(jwtTokenProvider.generateRefreshToken(eq(user), anyString()))
                .thenReturn("newRefreshTokenData");
        when(jwtTokenService.generateNewRefreshToken(user))
                .thenReturn("newRefreshTokenData");
        when(jwtTokenProvider.generateAccessToken(user))
                .thenReturn("newAccessToken");

        var result = jwtTokenService.refreshAndRotate(request, response);
        assertEquals("newAccessToken", result.accessToken());
        verify(refreshTokenRepository)
                .deleteByTokenId("tokenId");
        verify(refreshTokenRepository)
                .save(any(RefreshToken.class));
        verify(jwtTokenProvider)
                .attachRefreshTokenToCookie(
                        eq(response),
                        eq("newRefreshTokenData"));
    }

    @Test
    void refreshAndRotateInvalidToken_ShouldThrow() {
        when(jwtTokenProvider.extractRefreshTokenFromCookie(request))
                .thenReturn("refreshToken");
        when(jwtTokenProvider.extractRefreshTokenId("refreshToken"))
                .thenReturn("tokenId");
        when(jwtTokenProvider.extractUsername("refreshToken", JwtTokenType.REFRESH))
                .thenReturn("testuser");
        when(refreshTokenRepository.findByTokenId("tokenId"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class,
                () -> jwtTokenService.refreshAndRotate(request, response));
    }
}