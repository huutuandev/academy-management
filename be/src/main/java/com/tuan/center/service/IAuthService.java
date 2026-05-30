package com.tuan.center.service;

import com.tuan.center.dto.request.LoginRequest;
import com.tuan.center.dto.request.RegisterRequest;
import com.tuan.center.dto.response.AuthResponse;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
