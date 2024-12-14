package com.nivlalulu.nnpro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "password_reset_token")
@NoArgsConstructor
@Getter @Setter
public class PasswordResetToken {
    public static final int EXPIRATION_MILLIS = 15 * 60 * 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PasswordResetToken(String tokenHash, Date expiryDate, User user) {
        this.tokenHash = tokenHash;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public boolean isExpired(Date now) {
        return expiryDate.before(now);
    }
}
