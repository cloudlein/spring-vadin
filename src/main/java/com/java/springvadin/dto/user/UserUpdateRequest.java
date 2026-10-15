package com.java.springvadin.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {

    @Size(max = 255)
    private String username;


    @Size(max = 255)
    private String name;


    @Email(message = "Invalid email format")
    private  String email;


    @Size(min = 8, max = 255)
    private  String password;

    private  Long roleId;
}
