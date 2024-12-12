package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenId(String tokenId);
    void deleteByTokenId(String tokenId);
}
