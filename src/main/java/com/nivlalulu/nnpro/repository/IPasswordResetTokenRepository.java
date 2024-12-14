package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.PasswordResetToken;
import com.nivlalulu.nnpro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    Optional<PasswordResetToken> findByUser(User user);
}
