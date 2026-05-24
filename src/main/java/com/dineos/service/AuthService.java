package com.dineos.service;

import com.dineos.dto.request.LoginRequest;
import com.dineos.dto.request.RegisterRequest;
import com.dineos.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
