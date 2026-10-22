package com.java.springvadin.service;

import com.java.springvadin.dto.ApiResponse;
import com.java.springvadin.dto.auth.AuthResponse;
import com.java.springvadin.dto.auth.LoginRequest;
import com.java.springvadin.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);
    void logout(String token);
    AuthResponse refreshToken(String refreshToken);

}
