package com.secure_srm.repositories.security;

import com.secure_srm.model.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    // add custom JPA queries here
}
