package com.java.springvadin.service.impl;

import com.java.springvadin.dto.auth.AuthResponse;
import com.java.springvadin.dto.auth.LoginRequest;
import com.java.springvadin.dto.auth.RegisterRequest;
import com.java.springvadin.exception.ResourceAlreadyExistsException;
import com.java.springvadin.exception.ResourceNotFoundException;
import com.java.springvadin.mapper.UserMapper;
import com.java.springvadin.model.Role;
import com.java.springvadin.model.User;
import com.java.springvadin.repository.RoleRepository;
import com.java.springvadin.repository.UserRepository;
import com.java.springvadin.service.AuthService;
import com.java.springvadin.service.JwtService;
import com.java.springvadin.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenBlacklistService tokenBlacklistService;


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
                .message("login successful")
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional()
    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByName(registerRequest.getName())) {
            throw new ResourceAlreadyExistsException("Name already exists");
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("username already exists");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("email already exists");
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByRole("USER")
                .orElseThrow(() -> new ResourceNotFoundException("role not found"));
        user.setRole(role);
        userRepository.save(user);

        return AuthResponse.builder()
                .message("register successful")
                .build();
    }


    @Override
    public void logout(String token) {
        long remainingMillis = jwtService.extractExpiration(token).getTime() - System.currentTimeMillis();

        if (remainingMillis > 0) {
            tokenBlacklistService.addToBlacklist(token,remainingMillis);
        }
        SecurityContextHolder.clearContext();
    }
}
