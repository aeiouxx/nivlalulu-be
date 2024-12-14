package com.nivlalulu.nnpro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "password_reset_token")
@NoArgsConstructor
@Getter @Setter
public class PasswordResetToken {
    @Transient
    public static final int EXPIRATION_MILLIS = 15 * 60 * 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expiry_date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant expiryDate;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PasswordResetToken(String tokenHash, Instant expiryDate, User user) {
        this.tokenHash = tokenHash;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public boolean isExpired(Instant now) {
        return expiryDate.isBefore(now);
    }
}
