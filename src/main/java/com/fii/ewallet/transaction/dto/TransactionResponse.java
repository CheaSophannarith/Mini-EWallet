package com.fii.ewallet.transaction.dto;

import com.fii.ewallet.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long transactionId,
        String counterpartName,
        BigDecimal amount,
        TransactionType type,
        LocalDateTime date
){}
