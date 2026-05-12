package com.fii.ewallet.admin.dto;

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
