package com.java.springvadin.service.impl;

import com.java.springvadin.dto.auth.AuthResponse;
import com.java.springvadin.dto.auth.LoginRequest;
import com.java.springvadin.dto.auth.RegisterRequest;
import com.java.springvadin.exception.PasswordMismatchException;
import com.java.springvadin.model.User;
import com.java.springvadin.repository.UserRepository;
import com.java.springvadin.service.AuthService;
import com.java.springvadin.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .message("")
                .build()
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {

    }

    @Override
    public AuthResponse logout() {

    }
}
