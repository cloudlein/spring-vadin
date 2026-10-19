package com.java.springvadin.repository;

import com.java.springvadin.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    Boolean existByRoleName(String roleName);

    @Query("SELECT r FROM role r WHERE " +
            "LOWER(r.name) LIKE(CONCAT('%', :keywoard, '%'))")
    Page<Role> getAllByRoleName(@Param("keyword") String keyword, Pageable pageable);

}
