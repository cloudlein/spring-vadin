package com.java.springvadin.service;

import com.java.springvadin.dto.ApiResponse;
import com.java.springvadin.dto.PaginationResponse;
import com.java.springvadin.dto.role.RoleCreateRequest;
import com.java.springvadin.dto.role.RoleResponse;
import com.java.springvadin.dto.role.RoleUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {

    void create(RoleCreateRequest request);
    void update(Long id, RoleUpdateRequest request);
    void delete(Long id);
    RoleResponse getById(Long id);
    Page<RoleResponse> getAll(String search, Pageable pageable);

}
