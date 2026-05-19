package com.fii.ewallet.agent.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddMoneyToUserRequest(

        @NotNull(message = "Wallet ID is required")
        String walletId,

        @NotNull(message = "Balance is required")
        @DecimalMin(value = "1000", message = "Minimum amount is 1000")
        @DecimalMax(value = "99999999", message = "Maximum amount is 99999999")
        BigDecimal amount

) {}
