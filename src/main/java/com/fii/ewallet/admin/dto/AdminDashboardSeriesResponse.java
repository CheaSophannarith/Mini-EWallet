package com.fii.ewallet.admin.dto;

import java.util.List;

public record AdminDashboardSeriesResponse(
        String bucketType,
        List<AdminDashboardSeriesPoint> points
) {}
