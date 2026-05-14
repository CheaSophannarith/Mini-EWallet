package com.fii.ewallet.admin.dto;

import java.util.List;

public record UserResponse(
    String name,
    String email,
    String walletId,
    String role
) {}
