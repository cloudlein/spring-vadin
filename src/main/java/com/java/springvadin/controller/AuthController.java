package com.java.springvadin.controller;

import com.java.springvadin.dto.ApiResponse;
import com.java.springvadin.dto.auth.AuthResponse;
import com.java.springvadin.dto.auth.LoginRequest;
import com.java.springvadin.dto.auth.RefreshTokenRequest;
import com.java.springvadin.dto.auth.RegisterRequest;
import com.java.springvadin.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.success(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ApiResponse.success(authService.register(registerRequest));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if ( authHeader == null || !authHeader.startsWith("Bearer ")) {
           throw new IllegalArgumentException("Authorization header is invalid");
        }

        authService.logout(authHeader.substring(7));
        return ApiResponse.success("logout success", null);
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ApiResponse.success( authService.refreshToken(refreshTokenRequest.getRefreshToken()));
    }

}
