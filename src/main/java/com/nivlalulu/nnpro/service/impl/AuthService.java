package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.ConflictException;
import com.nivlalulu.nnpro.common.exceptions.UnauthorizedException;
import com.nivlalulu.nnpro.dto.v1.AuthenticationResponseDto;
import com.nivlalulu.nnpro.dto.v1.LoginRequestDto;
import com.nivlalulu.nnpro.dto.v1.RegistrationRequestDto;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.security.JwtTokenProvider;
import com.nivlalulu.nnpro.service.IAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String INVALID_CREDENTIALS = "Invalid username or password.";

    @Override
    public AuthenticationResponseDto login(LoginRequestDto login, HttpServletResponse response) {
        var user = userRepository.findByUsername(login.username())
                .orElseThrow(() -> {
                    log.debug("User not found: {}", login.username());
                    return new UnauthorizedException(INVALID_CREDENTIALS);
                });
        if (!passwordEncoder.matches(login.password(), user.getPassword())) {
            log.debug("Invalid password for user: {}", login.username());
            throw new UnauthorizedException(INVALID_CREDENTIALS);
        }

        var authenticationResponseDto = createResponseForUser(response, user);
        log.debug("User logged in successfully: {}", user);
        return authenticationResponseDto;
    }

    @Override
    public AuthenticationResponseDto register(RegistrationRequestDto registration, HttpServletResponse response) {
        if (userRepository.existsByUsername(registration.username())) {
            throw new ConflictException("User", "username", registration.username());
        }
        if (userRepository.existsByEmail(registration.email())) {
            throw new ConflictException("User", "email", registration.email());
        }

        var encodedPassword = passwordEncoder.encode(registration.password());
        var newUser = userRepository.save(new User(registration.username(), registration.email(), encodedPassword));

        var authenticationResponseDto = createResponseForUser(response, newUser);
        log.debug("User registered successfully: {}", newUser);
        return authenticationResponseDto;
    }

    private AuthenticationResponseDto createResponseForUser(HttpServletResponse response, User user) {
        String refreshToken = jwtTokenService.generateNewRefreshToken(user);
        jwtTokenProvider.attachRefreshTokenToCookie(response, refreshToken);

        var accessToken = jwtTokenProvider.generateAccessToken(user);
        var responseDto = new AuthenticationResponseDto(accessToken, user.getUsername());
        return responseDto;
    }
}
