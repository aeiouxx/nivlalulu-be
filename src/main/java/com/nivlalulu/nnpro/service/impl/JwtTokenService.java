package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.context.AccessTokenContextHolder;
import com.nivlalulu.nnpro.common.exceptions.ExpiredTokenException;
import com.nivlalulu.nnpro.common.exceptions.InvalidTokenException;
import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.dto.v1.TokenDto;
import com.nivlalulu.nnpro.model.RefreshToken;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IRefreshTokenRepository;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.security.JwtTokenProvider;
import com.nivlalulu.nnpro.security.JwtTokenType;
import com.nivlalulu.nnpro.security.service.ITokenBlacklistService;
import com.nivlalulu.nnpro.service.IJwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenService implements IJwtTokenService {
    private final IRefreshTokenRepository refreshTokenRepository;
    private final IUserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ITokenBlacklistService tokenBlacklistService;

    @Override
    public TokenDto refresh(HttpServletRequest request) {
        TokenData data = extractTokenData(request);
        var token = jwtTokenProvider.generateAccessToken(data.user);
        return token;
    }

    @Override
    @Transactional
    public TokenDto refreshAndInvalidate(String username,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        log.debug("Refreshing token for user with changed username: {}", username);
        var token = jwtTokenProvider.extractRefreshTokenFromCookie(request);
        String tokenId = jwtTokenProvider.extractRefreshTokenId(token);
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", "username", username));
        invalidateRefreshToken(tokenId);
        jwtTokenProvider.invalidateRefreshTokenCookie(response);
        return jwtTokenProvider.generateAccessToken(user);
    }

    @Override
    @Transactional
    public void logout(User user, HttpServletRequest request, HttpServletResponse response) {
        InvalidateRefreshTokenAndDeleteCookie(user, request, response);
        InvalidateAccessTokenIfNeeded(user);
    }

    private void InvalidateAccessTokenIfNeeded(User user) {
        var accessToken = AccessTokenContextHolder.getAccessToken();
        if (accessToken != null) {
            Instant expirationDate = jwtTokenProvider.extractExpiration(accessToken, JwtTokenType.ACCESS);
            Instant now = Instant.now();
            if (expirationDate.isAfter(now)) {
                var jti = jwtTokenProvider.extractJti(accessToken);
                Duration timeToLive = Duration.between(now, expirationDate);
                log.debug("Blacklisting access token: {}, for {}", jti, timeToLive.toString());
                tokenBlacklistService.blacklist(jti, timeToLive);
            } else {
                log.debug("Access token is already expired, no need to blacklist.");
            }
        } else {
            log.warn("No access token found for user {}", user.getUsername());
        }
    }

    private void InvalidateRefreshTokenAndDeleteCookie(User user, HttpServletRequest request, HttpServletResponse response) {
        var token = jwtTokenProvider.extractRefreshTokenFromCookie(request);
        if (token != null) {
            String tokenId = jwtTokenProvider.extractRefreshTokenId(token);
            invalidateRefreshToken(tokenId);
        }
        else {
            log.warn("No refresh token found for user {}", user.getUsername());
        }
        jwtTokenProvider.invalidateRefreshTokenCookie(response);
    }

    private record TokenData(String tokenId, User user) { }
    private TokenData extractTokenData(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new InvalidTokenException("No refresh token found");
        }
        log.debug("Received refresh token: {}", refreshToken);
        String tokenId = jwtTokenProvider.extractRefreshTokenId(refreshToken);
        String username = jwtTokenProvider.extractUsername(refreshToken, JwtTokenType.REFRESH);

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", "username", username));

        var token = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new NotFoundException(RefreshToken.class));

        if (!token.getUser().getUsername().equals(username)) {
            log.debug("Token {} is invalid", tokenId);
            throw new InvalidTokenException("Invalid token");
        }

        if (token.isExpired()) {
            log.debug("Token {} is expired", tokenId);
            throw new ExpiredTokenException("Refresh token expired");
        }

        TokenData tokenData = new TokenData(tokenId, user);
        return tokenData;
    }

    private void invalidateRefreshToken(String tokenId) {
        refreshTokenRepository.deleteByTokenId(tokenId);
        log.debug("Deleted old refresh token: {}", tokenId);
    }

    public String generateNewRefreshToken(User user) {
        String newRefreshTokenId = UUID.randomUUID().toString();
        String newRefreshTokenData = jwtTokenProvider.generateRefreshToken(user, newRefreshTokenId);
        var newRefreshToken = new RefreshToken(
                newRefreshTokenId,
                Instant.now().plus(JwtTokenProvider.REFRESH_EXPIRATION_TIME),
                user
        );
        refreshTokenRepository.save(newRefreshToken);
        log.debug("Saved new refresh token: {}", newRefreshTokenId);
        return newRefreshTokenData;
    }

}
