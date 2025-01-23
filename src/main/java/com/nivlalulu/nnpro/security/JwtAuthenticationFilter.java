package com.nivlalulu.nnpro.security;

import com.nivlalulu.nnpro.common.context.AccessTokenContextHolder;
import com.nivlalulu.nnpro.security.service.ITokenBlacklistService;
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
    private final ITokenBlacklistService tokenBlacklistService;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> NO_AUTH_PATHS = List.of(
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
        final String accessToken = jwtTokenProvider.extractAccessToken(request);
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
            var jti = jwtTokenProvider.extractJti(accessToken);
            if (tokenBlacklistService.isBlacklisted(jti)) {
                log.debug("Token is blacklisted, rejecting request");
                denyRequestInvalidToken(response);
                return;
            }
            Optional<UserDetails> userDetailsOpt = fetchDetailsIfAccessTokenValid(accessToken);
            if (userDetailsOpt.isEmpty()) {
                log.debug("Invalid token, rejecting request");
                denyRequestInvalidToken(response);
                return;
            }
            var details = userDetailsOpt.get();
            if (isUserDetailsInvalidSetErrorResponse(details, response)) {
                return;
            }
            setSecurityContext(request, accessToken, details);
            log.debug("Security context set, proceeding with request: {}", uri);
        } catch (Exception e) {
            log.error("Error occurred while setting security context", e);
            denyRequestInvalidToken(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     *  Checks whether the user details object is valid for authentication, i.e. not expired / locked / disabled...
     * @param details User details to check
     * @param response If user details are incorrect, sets the response status and message
     * @return true if request was denied, false if request should proceed
     */
    private boolean isUserDetailsInvalidSetErrorResponse(UserDetails details,
                                                         HttpServletResponse response) throws IOException {
        if (!details.isAccountNonExpired()) {
            log.debug("Account expired, rejecting request");
            denyRequestAccountExpired(response);
            return true;
        }

        if (!details.isAccountNonLocked()) {
            log.debug("Account locked, rejecting request");
            denyRequestAccountLocked(response);
            return true;
        }

        if (!details.isCredentialsNonExpired()) {
            log.debug("Credentials expired, rejecting request");
            denyRequestCredentialsExpired(response);
            return true;
        }

        if (!details.isEnabled()) {
            log.debug("Account disabled, rejecting request");
            denyRequestAccountDisabled(response);
            return true;
        }

        return false;
    }

    private void denyRequestInvalidToken(HttpServletResponse response) throws IOException {
        setErrorResponse(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "invalid_token",
                "The provided token is invalid"
        );
    }
    private void denyRequestAccountExpired(HttpServletResponse response) throws IOException {
        setErrorResponse(response,
                HttpServletResponse.SC_FORBIDDEN,
                "account_expired",
                "User account has expired.");
    }
    private void denyRequestAccountLocked(HttpServletResponse response) throws IOException {
        setErrorResponse(response,
                HttpServletResponse.SC_FORBIDDEN,
                "account_locked",
                "User account is locked.");
    }
    private void denyRequestAccountDisabled(HttpServletResponse response) throws IOException {
        setErrorResponse(response,
                HttpServletResponse.SC_FORBIDDEN,
                "account_disabled",
                "User account is disabled.");
    }
    private void denyRequestCredentialsExpired(HttpServletResponse response) throws IOException {
        setErrorResponse(response,
                HttpServletResponse.SC_FORBIDDEN,
                "credentials_expired",
                "User credentials have expired.");
    }

    private void setErrorResponse(HttpServletResponse response,
                                  int statusCode,
                                  String errorCode,
                                  String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String responseBody = String.format(
            "{\"error\": \"%s\", \"message\": \"%s\"}",
            errorCode,
            errorMessage
        );

        try (final var writer = response.getWriter()) {
            writer.write(responseBody);
            writer.flush();
        }
    }

    // todo: could use shouldNotFilter instead?
    private boolean isExcluded(String uri) {
        var isExcluded = NO_AUTH_PATHS.stream()
                .anyMatch(url -> pathMatcher.match(url, uri));
        log.debug("Request URI: '{}', excluded: {}", uri, isExcluded);
        return isExcluded;
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
        AccessTokenContextHolder.setAccessToken(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
