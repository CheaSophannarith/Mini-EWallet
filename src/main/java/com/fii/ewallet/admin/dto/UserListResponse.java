package com.fii.ewallet.admin.dto;

public record UserListResponse(
        Long id,
        String name,
        String email,
        String walletId,
        String role
){}
