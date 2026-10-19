package com.java.springvadin.service.impl;

import com.java.springvadin.dto.ApiResponse;
import com.java.springvadin.dto.role.RoleCreateRequest;
import com.java.springvadin.dto.role.RoleResponse;
import com.java.springvadin.dto.role.RoleUpdateRequest;
import com.java.springvadin.exception.ResourceAlreadyExistsException;
import com.java.springvadin.exception.ResourceNotFoundException;
import com.java.springvadin.mapper.RoleMapper;
import com.java.springvadin.model.Role;
import com.java.springvadin.repository.RoleRepository;
import com.java.springvadin.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public void create(RoleCreateRequest request) {
        if (checkIfRoleAlreadyExists(request.getRoleName())) {
            throw new ResourceAlreadyExistsException("Role already exists");
        }

        Role role = roleMapper.toEntity(request);
        roleRepository.save(role);
    }

    @Override
    public void update(Long id, RoleUpdateRequest request) {
        Role role = checkIfRoleExists(id);

        if (checkIfRoleAlreadyExists(request.getRoleName())) {
            throw new ResourceAlreadyExistsException("Role already in use");
        }

        role.setRole(request.getRoleName());
        roleRepository.save(role);

    }

    @Override
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found");
        }

        roleRepository.deleteById(id);
    }

    @Override
    public RoleResponse getById(Long id) {
       Role role = checkIfRoleExists(id);

       return roleRepository.findById(id)
               .map(roleMapper::toResponse)
               .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public Page<RoleResponse> getAll(String search, Pageable pageable) {
       Page<Role> rolePage;
       if (search != null && !search.trim().isEmpty()) {
           rolePage = roleRepository.getAllByRoleName(search.trim(), pageable);
       }else {
           rolePage = roleRepository.findAll(pageable);
       }

       return rolePage.map(roleMapper::toResponse);
    }


    private Boolean checkIfRoleAlreadyExists(String roleName) {
        return roleRepository.existByRoleName(roleName);
    }

    private Role checkIfRoleExists(Long roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

}
