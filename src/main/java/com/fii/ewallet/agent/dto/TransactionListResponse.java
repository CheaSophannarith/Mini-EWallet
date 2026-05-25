package com.fii.ewallet.agent.dto;

import com.fii.ewallet.enums.TransactionType;

import java.math.BigDecimal;

public record TransactionListResponse(
        Long transactionId,
        BigDecimal amount,
        String counterpartName,
        TransactionType type,
        java.time.LocalDateTime date
) {}
