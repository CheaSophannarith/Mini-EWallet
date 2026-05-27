package com.fii.ewallet.agent.dto;

import java.util.List;

public record AgentDashboardSeriesResponse(
        String bucketType,
        List<AgentDashboardSeriesPoint> points
) {}
