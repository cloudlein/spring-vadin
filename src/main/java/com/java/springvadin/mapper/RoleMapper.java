package com.java.springvadin.mapper;

import com.java.springvadin.dto.role.RoleCreateRequest;
import com.java.springvadin.dto.role.RoleResponse;
import com.java.springvadin.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toEntity(RoleCreateRequest request);

    @Mapping(source = "role", target = "user")
    RoleResponse toResponse(Role role);

}
