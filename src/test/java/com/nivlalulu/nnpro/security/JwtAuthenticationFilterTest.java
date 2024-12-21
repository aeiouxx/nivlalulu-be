package com.nivlalulu.nnpro.security;

import com.nivlalulu.nnpro.service.IJwtTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    UserDetailsService userDetailsService;
    @Mock
    private IJwtTokenService jwtTokenService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext(); // otherwise the context is shared between tests
    }


    @Test
    void noTokenPublicApi_ShouldProceed() throws ServletException, IOException {
        setupRequest("/public/endpoint", null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void noTokenSecuredApi_ShouldRespondUnauthorized() throws ServletException, IOException {
        setupRequest("/some-secured/endpoint", null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void validTokenSecuredApi_ShouldSetSecurityContext() throws ServletException, IOException {
        String token = "some_valid_token";
        String username = "user";
        boolean isValid = true;
        setupRequest("/some-secured/endpoint", token);
        mockToken(token, username, isValid);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
        assertTrue(authentication.isAuthenticated());
    }

    @Test
    void invalidTokenSecuredApi_ShouldRespondUnauthorized() throws ServletException, IOException {
        String token = "some_invalid_token";
        String username = "user";
        boolean isValid = false;
        setupRequest("/some-secured/endpoint", token);
        mockToken(token, username, isValid);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private void setupRequest(String uri, String token) {
        request.setRequestURI(uri);
        if (token != null) {
            request.addHeader("Authorization", "Bearer " + token);
        }
    }

    private void mockToken(String token, String username, boolean isValid) {
        UserDetails userDetails = new User(username, "password", List.of(new SimpleGrantedAuthority("test_role")));
        when(jwtTokenProvider.extractUsername(token, JwtTokenType.ACCESS))
                .thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);
        when(jwtTokenProvider.isTokenValid(token, JwtTokenType.ACCESS, userDetails))
                .thenReturn(isValid);
    }
}