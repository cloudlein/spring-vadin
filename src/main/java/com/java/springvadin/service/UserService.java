package com.java.springvadin.service;

import com.java.springvadin.dto.user.UserCreateRequest;
import com.java.springvadin.dto.user.UserResponse;
import com.java.springvadin.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;

public interface UserService {

    void create(UserCreateRequest request);
    void update(UserUpdateRequest request);
    void delete(Long id);
    UserResponse getById(Long id);
    Page<UserResponse>getAll(String search, int page, int pageSize);
}
