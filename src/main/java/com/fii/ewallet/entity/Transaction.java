package com.fii.ewallet.entity;

import com.fii.ewallet.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "status")
    private Status status;// PENDING, SUCCESS, FAILED

    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // 🔗 Sender (Many transactions → One user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    // 🔗 Receiver (Many transactions → One user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private User receiver;
}