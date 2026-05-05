package com.fii.ewallet.entity;

import com.fii.ewallet.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String role;

    @Column(name = "is_verified")
    private boolean isVerified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // 🔗 One-to-One with Wallet
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Wallet wallet;

    // 🔗 One-to-One with EmailVerification
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmailVerification emailVerification;

    // 🔗 Transactions sent
    @OneToMany(mappedBy = "sender")
    private List<Transaction> sentTransactions;

    // 🔗 Transactions received
    @OneToMany(mappedBy = "receiver")
    private List<Transaction> receivedTransactions;

    @OneToMany(mappedBy = "user")
    private List<RefreshToken> refreshTokens;
}