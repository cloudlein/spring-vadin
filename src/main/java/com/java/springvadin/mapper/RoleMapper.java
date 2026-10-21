package com.java.springvadin.mapper;

import com.java.springvadin.dto.role.RoleCreateRequest;
import com.java.springvadin.dto.role.RoleResponse;
import com.java.springvadin.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "roleName", target = "role")
    Role toEntity(RoleCreateRequest request);

    @Mapping(source = "role.role", target = "role")
    RoleResponse toResponse(Role role);

}
