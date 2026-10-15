package com.java.springvadin.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleCreateRequest {

    @NotBlank(message = "Role is required")
    private String roleName;

}
