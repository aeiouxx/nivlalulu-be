package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.InvalidTokenException;
import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.dto.v1.RefreshTokenResponseDto;
import com.nivlalulu.nnpro.model.RefreshToken;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IRefreshTokenRepository;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.security.JwtTokenProvider;
import com.nivlalulu.nnpro.security.JwtTokenType;
import com.nivlalulu.nnpro.service.IJwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenService implements IJwtTokenService {
    private final IRefreshTokenRepository refreshTokenRepository;
    private final IUserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private boolean isTokenValid(String tokenId, String username) {
        return refreshTokenRepository.findByTokenId(tokenId)
                .filter(token -> token.getUser().getUsername().equals(username) && !token.isExpired())
                .isPresent();
    }

    @Override
    @Transactional
    public RefreshTokenResponseDto refreshAndRotate(HttpServletRequest request, HttpServletResponse response) {
        TokenData tokenData = extractTokenData(request);
        invalidateRefreshToken(tokenData.tokenId);
        var newRefreshTokenData = generateNewRefreshToken(tokenData.user);
        jwtTokenProvider.attachRefreshTokenToCookie(response, newRefreshTokenData);
        var newAccessToken = jwtTokenProvider.generateAccessToken(tokenData.user);
        return new RefreshTokenResponseDto(newAccessToken);
    }

    @Override
    @Transactional
    public String refreshAndRotate(String username,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        log.debug("Refreshing token for user with changed username: {}", username);
        var token = jwtTokenProvider.extractRefreshTokenFromCookie(request);
        String tokenId = jwtTokenProvider.extractRefreshTokenId(token);
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", "username", username));
        invalidateRefreshToken(tokenId);
        var newRefreshTokenData = generateNewRefreshToken(user);
        jwtTokenProvider.attachRefreshTokenToCookie(response, newRefreshTokenData);
        return jwtTokenProvider.generateAccessToken(user);
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
        if (!isTokenValid(tokenId, username)) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", "username", username));
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
