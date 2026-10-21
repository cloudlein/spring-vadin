package com.java.springvadin.controller;

import com.java.springvadin.dto.ApiResponse;
import com.java.springvadin.dto.user.UserCreateRequest;
import com.java.springvadin.model.User;
import com.java.springvadin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<Void>createUser(@Valid @RequestBody UserCreateRequest request) {
        userService.create(request);
        return ApiResponse.success("user created successfully", null);
    }

    @PatchMapping("")


}
