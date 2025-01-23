package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.email.IMailSender;
import com.nivlalulu.nnpro.common.exceptions.ConflictException;
import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.exceptions.TooManyRequestsException;
import com.nivlalulu.nnpro.common.exceptions.UnauthorizedException;
import com.nivlalulu.nnpro.common.hashing.IHashProvider;
import com.nivlalulu.nnpro.model.PasswordResetToken;
import com.nivlalulu.nnpro.repository.IPasswordResetTokenRepository;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.security.service.IRateLimiter;
import com.nivlalulu.nnpro.service.IUserCredentialsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCredentialsService implements IUserCredentialsService {
    private final IUserRepository userRepository;
    private final IPasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final IMailSender mailSender;
    private final IHashProvider hashProvider;

    private final IRateLimiter rateLimiter;
    private final String PASSWORD_RESET_RATE_LIMIT_KEY = "password";
    private final int PASSWORD_RESET_RATE_LIMIT = 10;
    private final Duration PASSWORD_RESET_RATE_WINDOW = Duration.ofMinutes(5);


    @Override
    @Transactional
    public void createAndSendPasswordResetToken(String username) {
        String limitKey = PASSWORD_RESET_RATE_LIMIT_KEY + "-" + username;
        var now = Instant.now();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", "username", username));
        if (!rateLimiter.isAllowed(limitKey, PASSWORD_RESET_RATE_LIMIT, PASSWORD_RESET_RATE_WINDOW)) {
            log.info("Rate limit exceeded for user {}", username);
            throw new TooManyRequestsException("Too many password reset attempts. Please try again later.");
        }
        var data = generateToken(now);
        var token = passwordResetTokenRepository.findByUser(user)
                .map(existing -> {
                    existing.setTokenHash(data.hash);
                    existing.setExpiryDate(data.expiration);
                    return existing;
                })
                .orElseGet(() -> new PasswordResetToken(data.hash, data.expiration, user));
        log.debug("Generated token '{}' for user '{}', email '{}'.",
                token.getTokenHash(),
                user.getUsername(),
                user.getEmail());
        passwordResetTokenRepository.save(token);
        log.debug("Sending password reset email to user '{}', email '{}'", user.getUsername(), user.getEmail());
        mailSender.sendResetCode(user.getEmail(), data.token);
    }



    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        var hash = hashProvider.hash(token);
        var passwordResetToken = passwordResetTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> {
                    log.debug("Token does not exist");
                    return new UnauthorizedException("Invalid password reset token");
                });
        if (passwordResetToken.isExpired(Instant.now())) {
            log.debug("Token expired for user {}", passwordResetToken.getUser().getUsername());
            throw new UnauthorizedException("Invalid password reset token");
        }
        log.debug("Password reset token found for user {}",
                passwordResetToken.getUser().getUsername());
        var user = passwordResetToken.getUser();
        var newPasswordHash = passwordEncoder.encode(newPassword);
        user.setPassword(newPasswordHash);
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
        log.debug("Password reset for user {}", user.getUsername());
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", "username", username));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.info("Old password is incorrect for user {}", username);
            throw new UnauthorizedException("Password is incorrect");
        }
        var newHash = passwordEncoder.encode(newPassword);
        user.setPassword(newHash);
        log.debug("Password changed for user {}", username);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public String changeUsername(String username, String newUsername) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", "username", username));
        if (userRepository.existsByUsername(newUsername)) {
            log.info("Username {} already exists", newUsername);
            throw new ConflictException("User", "username", username);
        }
        user.setUsername(newUsername);
        var saved = userRepository.save(user);
        log.debug("Username changed for user {} to {}", username, saved.getUsername());
        return saved.getUsername();
    }

    @Override
    @Transactional
    public String changeEmail(String username, String newEmail) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", "username", username));
        if (userRepository.existsByEmail(newEmail)) {
            log.info("Email {} already exists", newEmail);
            throw new ConflictException("User", "getEmail", newEmail);
        }
        user.setEmail(newEmail);
        var saved = userRepository.save(user);
        log.debug("Email changed for user {} to {}", username, saved.getEmail());
        return saved.getEmail();
    }


    private record TokenData(String token, String hash, Instant expiration) {}
    private TokenData generateToken(Instant now) {
        var random = new SecureRandom();
        var bytes = new byte[64];
        random.nextBytes(bytes);
        var text = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        var hash = hashProvider.hash(text);
        var expiration = now.plusMillis(PasswordResetToken.EXPIRATION_MILLIS);
        return new TokenData(text, hash, expiration);
    }
}
