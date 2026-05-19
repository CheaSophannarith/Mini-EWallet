package com.fii.ewallet.agent.dto;

import com.fii.ewallet.enums.TransactionType;

public record TransactionListResponse(
        Long transactionId,
        Double amount,
        String counterpartName,
        TransactionType type,
        java.time.LocalDateTime date
) {}
