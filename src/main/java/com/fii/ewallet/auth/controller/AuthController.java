package com.fii.ewallet.auth.controller;

import com.fii.ewallet.auth.dto.RegisterRequest;
import com.fii.ewallet.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {

        Boolean result = authService.register(registerRequest);

        if (result) {
            return ResponseEntity.ok("User registered successfully, Please check your email to verify your account");
        }

        return ResponseEntity.badRequest().body("User registration failed");

    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {

        Boolean result = authService.verifyEmail(token);

        if (result) {
            return ResponseEntity.ok("Email verified successfully");
        }

        return ResponseEntity.badRequest().body("Email verification failed");

    }

}
