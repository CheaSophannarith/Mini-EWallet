package com.fii.ewallet.transaction.dto;

import com.fii.ewallet.enums.TransactionType;

import java.math.BigDecimal;

public record TransactionListResponse(
        Long transactionId,
        BigDecimal amount,
        String counterpartName,
        TransactionType type
) {}
