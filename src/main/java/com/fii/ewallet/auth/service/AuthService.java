package com.fii.ewallet.auth.service;

import com.fii.ewallet.auth.dto.*;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    void register(RegisterRequest registerRequest);

    void verifyEmail(String token);

    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);

    void logout(String refreshToken, HttpServletResponse response);

    LoginResponse refreshToken(String refreshToken);

    void changePassword(String email, ChangePasswordRequest request);

    void forgotPassword(String email);

    void resetPassword(String token, ResetPasswordRequest request);

}
