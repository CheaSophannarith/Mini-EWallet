package com.fii.ewallet.profile.dto;

import java.time.LocalDateTime;

public record ProfileImageResponse(String imageUrl, String message, int status, LocalDateTime timestamp) {
}
