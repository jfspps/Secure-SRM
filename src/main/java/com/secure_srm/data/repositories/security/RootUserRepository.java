package com.secure_srm.data.repositories.security;

import com.secure_srm.data.model.security.RootUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RootUserRepository extends JpaRepository<RootUser, Long> {
    Optional<RootUser> findByRootUserName(String username);
}
