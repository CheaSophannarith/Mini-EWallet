package com.fii.ewallet.admin.dto;

import com.fii.ewallet.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserTransactionListResponse(
        Long transactionId,
        BigDecimal amount,
        String counterpartName,
        TransactionType type,
        LocalDate date
) {}
