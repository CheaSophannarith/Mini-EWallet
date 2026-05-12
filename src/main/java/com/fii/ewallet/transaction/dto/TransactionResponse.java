package com.fii.ewallet.transaction.dto;

import com.fii.ewallet.enums.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponse(
        Long transactionId,
        String counterpartName,
        Double amount,
        TransactionType type,
        LocalDateTime date
){}
