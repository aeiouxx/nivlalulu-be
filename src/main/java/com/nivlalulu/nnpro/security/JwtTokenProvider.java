package com.nivlalulu.nnpro.security;

import com.nivlalulu.nnpro.dto.v1.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.service.RequestBodyService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    private final JwtKeyProvider jwtKeyProvider;
    private final UserDetailsService userDetailsService;
    private static final long ACCESS_EXPIRATION = 15 * 60 * 1000;
    private static final long REFRESH_EXPIRATION = 7L * 24L * 60L * 60L * 1000L;
    public static final Duration ACCESS_EXPIRATION_TIME = Duration.ofMillis(ACCESS_EXPIRATION);
    public static final Duration REFRESH_EXPIRATION_TIME = Duration.ofMillis(REFRESH_EXPIRATION);

    private static final String CLAIM_AUTHORITIES = "authorities";
    private static final String CLAIM_REFRESH_ID = "rtid";

    private static final String COOKIE_REFRESH_KEY = "refresh_token";

    // > Refresh
    public void attachRefreshTokenToCookie(
            HttpServletResponse response,
            String refreshToken) {
        Cookie cookie = new Cookie(COOKIE_REFRESH_KEY, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) REFRESH_EXPIRATION_TIME.toSeconds());
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }

    public void invalidateRefreshTokenCookie(
            HttpServletResponse response
    ) {
        Cookie cookie = new Cookie(COOKIE_REFRESH_KEY, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            log.debug("No cookies found in request, but were expected.");
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> COOKIE_REFRESH_KEY.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public String generateRefreshToken(
            UserDetails userDetails,
            String refreshTokenId) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_REFRESH_ID, refreshTokenId);
        var token = generateToken(
                claims,
                userDetails.getUsername(),
                jwtKeyProvider.getKey(JwtTokenType.REFRESH),
                REFRESH_EXPIRATION);
        return token.content();
    }
    public String extractRefreshTokenId(String token) {
        return extractSignedClaim(token, JwtTokenType.REFRESH, claims -> claims.get(CLAIM_REFRESH_ID, String.class));
    }
    // < Refresh
    // > Access
    public TokenDto generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(userDetails.getUsername(), userDetails.getAuthorities());
    }
    public TokenDto generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_AUTHORITIES,
                authorities
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                );
        return generateToken(
                claims,
                username,
                jwtKeyProvider.getKey(JwtTokenType.ACCESS),
                ACCESS_EXPIRATION);
    }
    // < Access
    // > Common
    public boolean isTokenValid(String token, JwtTokenType type) {
        var user = extractUsername(token, type);
        var details = userDetailsService.loadUserByUsername(user);
        return isTokenValid(token, type, details);
    }
    public boolean isTokenValid(String token, JwtTokenType type, UserDetails userDetails) {
        final SecretKey key = jwtKeyProvider.getKey(type);
        return isTokenValid(token, key, userDetails);
    }
    public String extractUsername(String token, JwtTokenType type) {
        return extractSignedClaim(token, type, Claims::getSubject);
    }
    private TokenDto generateToken(Map<String, Object> claims, String subject, SecretKey key, long expirationMillis) {
        var issuedAt = new Date();
        var expiration = new Date(System.currentTimeMillis() + expirationMillis);
        var secondsBuffer = 1;
        var secondsLifetime = (expirationMillis / 1000) + secondsBuffer;
        var token = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();

        return new TokenDto(
                token,
                issuedAt.toInstant(),
                expiration.toInstant(),
                secondsLifetime
        );
    }
    public <T> T extractSignedClaim(String token, JwtTokenType type, Function<Claims, T> resolver) {
        log.debug("Extracting claim from {} token: {}", type, token);
        final SecretKey key = jwtKeyProvider.getKey(type);
        final Claims claims = extractSignedClaims(token, key);
        return resolver.apply(claims);
    }

    private boolean isTokenValid(String token, SecretKey key, UserDetails userDetails) {
        if (userDetails == null) return false;
        final String username = extractUsername(token, key);
        if (username == null) {
            return false;
        }
        final boolean notExpired = !isTokenExpired(token, key);
        final boolean usernameMatches = username.equals(userDetails.getUsername());
        return notExpired && usernameMatches;
    }

    private String extractUsername(String token, SecretKey key) {
        return extractSignedClaim(token, key, Claims::getSubject);
    }

    private boolean isTokenExpired(String token, SecretKey key) {
        try {
            return extractSignedClaim(token, key, Claims::getExpiration).before(new Date());
        } catch (ExpiredJwtException e) {
            log.debug("Token expired: {}", e.getMessage());
            return true;
        }
    }

    private <T> T extractSignedClaim(String token, SecretKey key, Function<Claims, T> resolver) {
        final Claims claims = extractSignedClaims(token, key);
        return resolver.apply(claims);
    }

    private Claims extractSignedClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    // < Common
}
