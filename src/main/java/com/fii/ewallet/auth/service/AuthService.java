package com.fii.ewallet.auth.service;

import com.fii.ewallet.auth.dto.RegisterRequest;

public interface AuthService {

    public Boolean register(RegisterRequest registerRequest);

    public Boolean verifyEmail(String token);

}
