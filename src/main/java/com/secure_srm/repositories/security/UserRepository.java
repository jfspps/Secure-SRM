package com.secure_srm.repositories.security;

import com.secure_srm.model.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    // add custom JPA queries here
    Optional<User> findByUsername(String username);

    Set<User> findAllByUsername(String username);
}
