package com.java.springvadin.mapper;

import com.java.springvadin.dto.auth.RegisterRequest;
import com.java.springvadin.dto.user.UserCreateRequest;
import com.java.springvadin.dto.user.UserResponse;
import com.java.springvadin.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(RegisterRequest request);

    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.role", target = "role")
    UserResponse toResponse(User user);
}
