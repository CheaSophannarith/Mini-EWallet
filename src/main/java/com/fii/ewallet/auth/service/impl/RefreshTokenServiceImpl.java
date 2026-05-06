package com.fii.ewallet.auth.service.impl;

import com.fii.ewallet.auth.service.RefreshTokenService;
import com.fii.ewallet.auth.repository.RefreshTokenRepository;
import com.fii.ewallet.entity.RefreshToken;
import com.fii.ewallet.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();

        String token = UUID.randomUUID().toString();

        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);

        return token;
    }
}