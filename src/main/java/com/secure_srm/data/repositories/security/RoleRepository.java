package com.secure_srm.data.repositories.security;

import com.secure_srm.data.model.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // add custom JPA queries here
    Optional<Role> findByRoleName(String roleName);
}
