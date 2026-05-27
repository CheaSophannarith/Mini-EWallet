package com.fii.ewallet.admin.dto;

import java.math.BigDecimal;

public record AdminDashboardSummaryResponse(
        long totalUsers,
        long totalAgents,
        long newUsers,
        long newAgents,
        long transactionsCount,
        long failedTransactions,
        BigDecimal totalVolume
) {}
