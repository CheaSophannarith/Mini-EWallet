package com.fii.ewallet.auth.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class LoginResponse {

    private String token;
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public LoginResponse(String token, String message, int status, LocalDateTime timestamp) {
        this.token = token;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

}
