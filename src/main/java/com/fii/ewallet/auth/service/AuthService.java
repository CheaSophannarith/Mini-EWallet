package com.fii.ewallet.auth.service;

import com.fii.ewallet.auth.dto.LoginRequest;
import com.fii.ewallet.auth.dto.LoginResponse;
import com.fii.ewallet.auth.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    void register(RegisterRequest registerRequest);

    void verifyEmail(String token);

    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);

    void logout(String refreshToken, HttpServletResponse response);

}
