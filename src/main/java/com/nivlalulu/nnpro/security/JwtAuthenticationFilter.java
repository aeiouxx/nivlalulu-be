package com.nivlalulu.nnpro.security;

import com.nivlalulu.nnpro.dto.v1.RefreshTokenResponseDto;
import com.nivlalulu.nnpro.service.IJwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final IJwtTokenService jwtTokenService;

    private static final List<String> NO_AUTH_URLS = List.of(
            "/api/public/"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        var uri = request.getRequestURI();
        log.debug("Request URI: {}", uri);
        if (isExcluded(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String accessToken = extractAccessToken(request);
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            final String username = jwtTokenProvider.extractUsername(accessToken, JwtTokenType.ACCESS);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                setSecurityContext(request, accessToken, username);
            }
        } catch (Exception e) {
            log.error("Error occurred while setting security context", e);
            setInvalidTokenResponse(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * @deprecated This method is deprecated and will be removed in the future, clients should attempt a silent
     * refresh via the refresh token endpoint instead.
     * @param request
     * @param response
     * @return
     */
    @Deprecated(forRemoval = true)
    private boolean tryTokenRefresh(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Attempting to refresh token");
        try {
            RefreshTokenResponseDto refreshTokenResponse = jwtTokenService.refreshAndRotate(request, response);
            response.setHeader("Authorization", "Bearer " + refreshTokenResponse.accessToken());
            return true;
        } catch (Exception e) {
            log.error("Error occurred while refreshing token", e);
            return false;
        }
    }

    private void setInvalidTokenResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (final var writer = response.getWriter()) {
            writer.write("Invalid token");
            writer.flush();
        }
    }

    private boolean isExcluded(String uri) {
        for (String url : NO_AUTH_URLS) {
            if (uri.contains(url)) {
                // shouldn't even be getting triggered if the configuration is correct
                log.error("[WRONG CONFIGURATION!!!] Excluded URL: {}", uri);
                return true;
            }
        }
        return false;
    }

    private String extractAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }

    private void setSecurityContext(HttpServletRequest request, String token, String username) {
        UserDetails details = userDetailsService.loadUserByUsername(username);
        if (!jwtTokenProvider.isTokenValid(token, JwtTokenType.ACCESS, details)) {
            return;
        }
        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
