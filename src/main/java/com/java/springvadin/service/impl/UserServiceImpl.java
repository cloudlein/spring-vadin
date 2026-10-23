package com.java.springvadin.service.impl;

import com.java.springvadin.dto.user.UserCreateRequest;
import com.java.springvadin.dto.user.UserResponse;
import com.java.springvadin.dto.user.UserUpdateRequest;
import com.java.springvadin.exception.ResourceAlreadyExistsException;
import com.java.springvadin.exception.ResourceNotFoundException;
import com.java.springvadin.mapper.UserMapper;
import com.java.springvadin.model.Role;
import com.java.springvadin.model.User;
import com.java.springvadin.repository.RoleRepository;
import com.java.springvadin.repository.UserRepository;
import com.java.springvadin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
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

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("role not found"));
            user.setRole(role);
        }

        userRepository.save(user);

    }

    @Transactional
    @Override
    public void update(Long id, UserUpdateRequest request) {
        User user = checkIfUserExists(id);
        if (request.getName() != null && !request.getName().equals(user.getName())) {
            if (checkIfNameAlreadyExists(request.getName())) {
                throw new ResourceAlreadyExistsException("name already exists");
            }
            user.setName(request.getName());
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (checkIfUsernameAlreadyExists(request.getUsername())) {
                throw new ResourceAlreadyExistsException("username already exists");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (checkIfEmailAlreadyExists(request.getEmail())) {
                throw new ResourceAlreadyExistsException("email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getIsTotpEnabled() != null && !request.getIsTotpEnabled().equals(user.getIsTotpEnabled())) {
            user.setIsTotpEnabled(request.getIsTotpEnabled());
        }

        if (request.getRoleId() != null && (user.getRole() == null || !request.getRoleId().equals(user.getRole().getId()))) {
            Role role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> new ResourceNotFoundException("role not found"));
            user.setRole(role);
        }

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        User user = checkIfUserExists(id);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getById(Long id) {
        User user = checkIfUserExists(id);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserResponse> getAll(String search, Pageable pageable) {
        Page<User> userPage;
        if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.getAllUserByNameUsernameEmail(search, pageable);
        }else {
            userPage = userRepository.findAll(pageable);
        }

        return  userPage.map(userMapper::toResponse);
    }

    private Boolean checkIfEmailAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private Boolean checkIfNameAlreadyExists(String name) {
        return userRepository.existsByName(name);
    }

    private Boolean checkIfUsernameAlreadyExists(String username) {
        return userRepository.existsByUsername(username);
    }

    private User checkIfUserExists(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

}
