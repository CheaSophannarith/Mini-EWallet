package com.fii.ewallet.auth.service.impl;

import com.fii.ewallet.auth.dto.LoginRequest;
import com.fii.ewallet.auth.dto.LoginResponse;
import com.fii.ewallet.auth.dto.RegisterRequest;
import com.fii.ewallet.repository.RefreshTokenRepository;
import com.fii.ewallet.repository.UserRepository;
import com.fii.ewallet.auth.service.AuthService;
import com.fii.ewallet.auth.service.RefreshTokenService;
import com.fii.ewallet.email.service.EmailService;
import com.fii.ewallet.repository.EmailVerificationRepository;
import com.fii.ewallet.entity.EmailVerification;
import com.fii.ewallet.entity.RefreshToken;
import com.fii.ewallet.entity.User;
import com.fii.ewallet.enums.Role;
import com.fii.ewallet.exception.EmailAlreadyInUsedException;
import com.fii.ewallet.exception.EmailIsNotVerified;
import com.fii.ewallet.jwt.service.JwtService;
import com.fii.ewallet.wallet.service.WalletService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final WalletService walletService;

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {

        User existing = userRepository.findByEmail(registerRequest.email()).orElse(null);

        if (existing != null && existing.isVerified()) {
            throw new EmailAlreadyInUsedException("Email already taken");
        }

        if (existing != null && !existing.isVerified()) {
            throw new EmailIsNotVerified("Email is not verified");
        }

        User user = new User();
        user.setName(registerRequest.name());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setVerified(false);
        user.setRole(String.valueOf(Role.USER));

        User createdUser = userRepository.save(user);

        String token = UUID.randomUUID().toString();

        EmailVerification ev = new EmailVerification();
        ev.setToken(token);
        ev.setUser(createdUser);
        ev.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        emailVerificationRepository.save(ev);

        String link = "http://localhost:8080/api/v1/auth/verify?token=" + token;

        emailService.sendVerificationEmail(user.getEmail(), link);

    }

    @Override
    @Transactional
    public void verifyEmail(String token) {

        EmailVerification ev = emailVerificationRepository.findByToken(token).orElseThrow(
                () -> new EmailIsNotVerified("Invalid token!")
        );

        if (ev.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new EmailIsNotVerified("Token has expired!");
        }

        User user = ev.getUser();
        user.setVerified(true);
        userRepository.save(user);

        walletService.createWallet(user);

        emailVerificationRepository.delete(ev);

    }

    @Override
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        if (!user.isVerified()) {
            throw new EmailIsNotVerified("Email is not verified");
        }

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production (HTTPS)
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

        response.addCookie(cookie);

        LoginResponse responseLogin = new LoginResponse(
                accessToken,
                "Login successfully",
                HttpStatus.OK.value(),
                LocalDateTime.now()
        );

        return responseLogin;

    }

    @Override
    public void logout(String refreshToken, HttpServletResponse response) {

        if(refreshToken != null) {

            RefreshToken rt = refreshTokenRepository.findByToken(refreshToken);

            if(rt != null) {
                rt.setIsRevoked(true);
                refreshTokenRepository.save(rt);
            }

        }

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {

        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken);

        if (rt == null || !rt.getExpiresAt().isAfter(LocalDateTime.now()) || rt.getIsRevoked()) {
            throw new UsernameNotFoundException("Invalid refresh token");
        }

        User user = rt.getUser();
        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponse(accessToken, "Token refreshed successfully", HttpStatus.OK.value(), LocalDateTime.now());

    }
}

