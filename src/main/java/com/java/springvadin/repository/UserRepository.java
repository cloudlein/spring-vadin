package com.java.springvadin.repository;

import com.java.springvadin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Boolean checkIfUsernameAlreadyExists(String username);
    Boolean checkIfNameAlreadyExists(String username);
    Boolean checkIfEmailExists(String email);

    Optional<User> findByUsername(String username);
}
