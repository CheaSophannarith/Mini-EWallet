package com.fii.ewallet.admin.dto;

import com.fii.ewallet.enums.Status;

import java.time.LocalDateTime;

public record AdminTransactionResponse(
        Long transactionId,
        String senderName,
        String receiverName,
        Double amount,
        Status status,
        LocalDateTime createdAt
) {}
