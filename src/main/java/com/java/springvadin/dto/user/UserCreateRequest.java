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
public class UserCreateRequest {

    @NotBlank(message = "username is required")
    @Size(max = 255)
    private String username;

    @NotBlank(message = "name is required")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "Invalid email format")
    private  String email;

    @NotBlank(message = "passsword is required")
    @Size(min = 8, max = 255)
    private  String password;

    @NotNull(message = "role id is required")
    private  Long roleId;


}
