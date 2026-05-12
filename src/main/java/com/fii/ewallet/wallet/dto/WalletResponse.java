package com.fii.ewallet.wallet.dto;

import java.math.BigDecimal;

public record WalletResponse(
        Long id,
        BigDecimal balance,
        String walletId
) {
}
