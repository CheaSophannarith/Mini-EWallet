package com.fii.ewallet.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionListResponse(

        Long id,
        BigDecimal amount,
        String senderId,
        String receiverId,
        String senderName,
        String receiverName,
        LocalDateTime date

){}
