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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final IJwtTokenService jwtTokenService;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private static List<String> NO_AUTH_PATHS = List.of(
            "/api/swagger-ui/**",
            "/api/v3/api-docs/**",
            "/api/public/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        var uri = request.getRequestURI();
        if (isExcluded(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        log.debug("Secured URL: '{}', verifying token", uri);
        final String accessToken = extractAccessToken(request);
        if (accessToken == null) {
            log.debug("Access token is missing, rejecting request");
            denyRequestInvalidToken(response);
            return;
        }
        try {
            var existingAuth = SecurityContextHolder.getContext().getAuthentication();
            if (existingAuth != null) {
                log.error("Security context set already, this should not happen!");
                return;
            }

            Optional<UserDetails> userDetails = fetchDetailsIfAccessTokenValid(accessToken);
            if (userDetails.isEmpty()) {
                log.debug("Invalid token, rejecting request");
                denyRequestInvalidToken(response);
                return;
            }
            setSecurityContext(request, accessToken, userDetails.get());
            log.debug("Security context set, proceeding with request: {}", uri);
        } catch (Exception e) {
            log.error("Error occurred while setting security context", e);
            denyRequestInvalidToken(response);
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

    private void denyRequestInvalidToken(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (final var writer = response.getWriter()) {
            writer.write("Invalid token");
            writer.flush();
        }
    }

    private boolean isExcluded(String uri) {
        var isExcluded = NO_AUTH_PATHS.stream()
                        .anyMatch(url -> pathMatcher.match(url, uri));
        log.debug("Request URI: '{}', excluded: {}", uri, isExcluded);
        return isExcluded;
    }

    private String extractAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }

    private Optional<UserDetails> fetchDetailsIfAccessTokenValid(String token) {
        UserDetails userDetails;
        try {
            var username = jwtTokenProvider.extractUsername(token, JwtTokenType.ACCESS);
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            log.error("Error retrieving user details: '{}'", e.getMessage());
            return Optional.empty();
        }
        return jwtTokenProvider.isTokenValid(token, JwtTokenType.ACCESS, userDetails)
                ? Optional.of(userDetails)
                : Optional.empty();
    }

    private void setSecurityContext(HttpServletRequest request, String token, UserDetails details) {
        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
