package com.fii.ewallet.agent.dto;

import java.math.BigDecimal;

public record AgentDashboardSummaryResponse(
        BigDecimal walletBalance,
        long depositCount,
        BigDecimal depositVolume
) {}
