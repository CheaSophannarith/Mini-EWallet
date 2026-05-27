package com.fii.ewallet.agent.dto;

import java.math.BigDecimal;

public record AgentDashboardSeriesPoint(
        String bucket,
        long count,
        BigDecimal volume
) {}
