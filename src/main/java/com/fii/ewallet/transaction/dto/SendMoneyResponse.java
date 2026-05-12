package com.fii.ewallet.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SendMoneyResponse(
        String message,
        BigDecimal newBalance,
        LocalDateTime transactionTime
) {}
