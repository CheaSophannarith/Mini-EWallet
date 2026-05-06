package com.fii.ewallet.common;

import java.time.LocalDateTime;

public record ApiResponse(String message, int status, LocalDateTime timestamp) {
}
