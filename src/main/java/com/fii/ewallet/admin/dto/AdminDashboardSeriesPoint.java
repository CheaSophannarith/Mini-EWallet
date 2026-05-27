package com.fii.ewallet.admin.dto;

import java.math.BigDecimal;

public record AdminDashboardSeriesPoint(
        String bucket,
        long count,
        BigDecimal volume
) {}
