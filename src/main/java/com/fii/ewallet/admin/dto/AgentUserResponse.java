package com.fii.ewallet.admin.dto;

import java.time.LocalDateTime;

public record AgentUserResponse(
        String name,
        String email,
        String role,
        LocalDateTime createdAt
) {}
