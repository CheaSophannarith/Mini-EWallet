package com.fii.ewallet.auth.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class LoginResponse {

    private String token;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String role;
    private String name;
    private String email;

    public LoginResponse(String token, String message, int status, LocalDateTime timestamp, String role, String name, String email) {
        this.token = token;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.role = role;
        this.name = name;
        this.email = email;
    }

}
