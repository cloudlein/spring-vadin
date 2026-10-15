package com.java.springvadin.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {

    private Long id;

    private String username;

    private String name;

    private String email;

    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
}

