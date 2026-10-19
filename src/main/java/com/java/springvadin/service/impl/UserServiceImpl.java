package com.java.springvadin.service.impl;

import com.java.springvadin.dto.user.UserCreateRequest;
import com.java.springvadin.dto.user.UserResponse;
import com.java.springvadin.dto.user.UserUpdateRequest;
import com.java.springvadin.exception.ResourceAlreadyExistsException;
import com.java.springvadin.mapper.UserMapper;
import com.java.springvadin.repository.RoleRepository;
import com.java.springvadin.repository.UserRepository;
import com.java.springvadin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void create(UserCreateRequest request) {
        if (checkIfNameAlreadyExists(request.getName())) {
            throw new ResourceAlreadyExistsException("name already exists");
        }

        if (checkIfUsernameAlreadyExists(request.getUsername())) {
            throw new ResourceAlreadyExistsException("username already exists");
        }

        if (checkIfEmailAlreadyExists(request.getEmail())) {
            throw new ResourceAlreadyExistsException("email already exists");
        }
        


    }

    @Override
    public void update(Long id, UserUpdateRequest request) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public UserResponse getById(Long id) {
        return null;
    }

    @Override
    public Page<UserResponse> getAll(String search, Pageable pageable) {
        return null;
    }

    private Boolean checkIfEmailAlreadyExists(String email) {
        return userRepository.checkIfEmailExists(email);
    }

    private Boolean checkIfNameAlreadyExists(String name) {
        return userRepository.checkIfNameAlreadyExists(name);
    }

    private Boolean checkIfUsernameAlreadyExists(String username) {
        return userRepository.checkIfUsernameAlreadyExists(username);
    }

}
