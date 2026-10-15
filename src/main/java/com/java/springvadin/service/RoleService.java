package com.java.springvadin.service;

import com.java.springvadin.dto.ApiResponse;
import com.java.springvadin.dto.PaginationResponse;
import com.java.springvadin.dto.role.RoleCreateRequest;
import com.java.springvadin.dto.role.RoleResponse;
import com.java.springvadin.dto.role.RoleUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {

    void create(RoleCreateRequest request);
    void update(RoleUpdateRequest request);
    void delete(Long id);
    ApiResponse<RoleResponse> get(Long id);
    Page<RoleResponse> getAll(String search, int page, int pageSize);

}
