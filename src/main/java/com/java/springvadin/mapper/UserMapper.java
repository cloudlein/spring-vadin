package com.java.springvadin.mapper;

import com.java.springvadin.dto.user.UserCreateRequest;
import com.java.springvadin.dto.user.UserResponse;
import com.java.springvadin.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserCreateRequest request);

    @Mapping(source = "role", target = "role")
    UserResponse toResponse(UserCreateRequest request);

}
