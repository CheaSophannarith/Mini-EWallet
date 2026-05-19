package com.fii.ewallet.admin.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record AddBalanceToAgentWalletRequest(
        @NotNull(message = "Balance is required")
        @DecimalMin(value = "1000", message = "Minimum amount is 1000")
        @DecimalMax(value = "99999999", message = "Maximum amount is 99999999")
        Double balance
) {}
