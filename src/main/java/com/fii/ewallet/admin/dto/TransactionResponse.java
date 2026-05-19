package com.fii.ewallet.admin.dto;

import com.fii.ewallet.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(

        BigDecimal amount,
        String senderId,
        String receiverId,
        String senderName,
        String receiverName,
        LocalDateTime date

){}
