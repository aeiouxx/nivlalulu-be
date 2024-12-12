package com.nivlalulu.nnpro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter @Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false, unique = true)
    private String tokenId;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public RefreshToken(String tokenId, Instant expiryDate, User user) {
        this.tokenId = tokenId;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}
