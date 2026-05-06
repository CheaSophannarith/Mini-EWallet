package com.fii.ewallet.auth.controller;

import com.fii.ewallet.auth.dto.LoginRequest;
import com.fii.ewallet.auth.dto.LoginResponse;
import com.fii.ewallet.auth.dto.RegisterRequest;
import com.fii.ewallet.auth.service.AuthService;
import com.fii.ewallet.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {

        authService.register(registerRequest);

        ApiResponse response = new ApiResponse(
                "User registered successfully, Please check your email to verify your account",
                HttpStatus.CREATED.value(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam("token") String token) {

        authService.verifyEmail(token);

        ApiResponse response = new ApiResponse(
                "Email verified successfully",
                HttpStatus.OK.value(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        LoginResponse loginResponse = authService.login(loginRequest, response);

        return ResponseEntity.ok(loginResponse);

    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        authService.logout(refreshToken, response);

        return ResponseEntity.ok(new ApiResponse("Logged out successfully", HttpStatus.OK.value(), LocalDateTime.now()));

    }

    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome to the e-wallet API!");
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {

        LoginResponse loginResponse = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(loginResponse);

    }

}
