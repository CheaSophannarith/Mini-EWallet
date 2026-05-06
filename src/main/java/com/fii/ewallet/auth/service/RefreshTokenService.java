package com.fii.ewallet.auth.service;

import com.fii.ewallet.entity.User;


public interface RefreshTokenService {

    String createRefreshToken(User user);

}
