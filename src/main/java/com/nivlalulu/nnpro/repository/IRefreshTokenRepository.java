package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenId(String tokenId);
    void deleteByTokenId(String tokenId);
}
