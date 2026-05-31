package com.tuan.center.service;

import com.tuan.center.dto.request.LoginRequest;
import com.tuan.center.dto.request.RegisterRequest;
import com.tuan.center.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshAccessToken(String refreshToken);
    void logout(String refreshToken);
}
