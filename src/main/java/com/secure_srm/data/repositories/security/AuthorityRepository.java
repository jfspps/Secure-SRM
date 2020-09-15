package com.secure_srm.data.repositories.security;

import com.secure_srm.data.model.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    // add custom JPA queries here
}
