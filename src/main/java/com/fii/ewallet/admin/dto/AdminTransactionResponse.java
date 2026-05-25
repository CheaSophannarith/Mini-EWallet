package com.fii.ewallet.admin.dto;

import com.fii.ewallet.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminTransactionResponse(
        Long transactionId,
        String senderName,
        String receiverName,
        BigDecimal amount,
        Status status,
        LocalDateTime createdAt
) {}
