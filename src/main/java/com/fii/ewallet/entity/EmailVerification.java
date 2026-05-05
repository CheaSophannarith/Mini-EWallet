package com.fii.ewallet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
@Getter
@Setter
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    // 🔗 FK to User
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}