package com.fii.ewallet.auth.service.impl;

import com.fii.ewallet.auth.dto.RegisterRequest;
import com.fii.ewallet.auth.repository.UserRepository;
import com.fii.ewallet.auth.service.AuthService;
import com.fii.ewallet.email.EmailService;
import com.fii.ewallet.email.repository.EmailVerificationRepository;
import com.fii.ewallet.entity.EmailVerification;
import com.fii.ewallet.entity.User;
import com.fii.ewallet.exception.EmailAlreadyInUsedException;
import com.fii.ewallet.exception.EmailIsNotVerified;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository;

    @Override
    public Boolean register(RegisterRequest registerRequest) {

        User existing = userRepository.findByEmail(registerRequest.email()).orElse(null);

        if (existing != null && existing.isVerified()) {
            throw new EmailAlreadyInUsedException("Email already taken");
        }

        if (existing != null && !existing.isVerified()) {
            throw new EmailIsNotVerified("Email is not verified");
        }

        User user = new User();
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.isVerified();

        User createdUser = userRepository.save(user);

        String token = UUID.randomUUID().toString();

        EmailVerification ev = new EmailVerification();
        ev.setToken(token);
        ev.setUser(createdUser);
        ev.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        emailVerificationRepository.save(ev);

        String link = "http://localhost:8080/api/v1/auth/verify?token=" + token;

        emailService.sendVerificationEmail(user.getEmail(), link);

        if (createdUser != null) {
            return true;
        }

        return false;

    }

    @Override
    public Boolean verifyEmail(String token) {

        EmailVerification ev = emailVerificationRepository.findByToken(token).orElseThrow(
                () -> new EmailIsNotVerified("Invalid token!")
        );

        if (ev.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new EmailIsNotVerified("Token has expired!");
        }

        User user = ev.getUser();
        user.setVerified(true);
        User updatedUser = userRepository.save(user);

        emailVerificationRepository.delete(ev);

        return updatedUser.isVerified();

    }
}
