package com.nivlalulu.nnpro.security;

import com.nivlalulu.nnpro.security.service.ITokenBlacklistService;
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
    private final ITokenBlacklistService tokenBlacklistService;
    private final UserDetailsService userDetailsService;

    private static final List<String> NO_AUTH_URLS = List.of(
            "/api/auth"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (isExcluded(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        final String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jti = jwtTokenProvider.extractJti(token);
        if (tokenBlacklistService.isBlacklisted(jti)) {
            setInvalidTokenResponse(response);
            return;
        }
        final String username = jwtTokenProvider.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            setSecurityContext(request, token, username);
        }
        filterChain.doFilter(request, response);
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
                return true;
            }
        }
        return false;
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }

    private void setSecurityContext(HttpServletRequest request, String token, String username) {
        UserDetails details = userDetailsService.loadUserByUsername(username);
        if (!jwtTokenProvider.isTokenValid(token, details)) {
            return;
        }
        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
