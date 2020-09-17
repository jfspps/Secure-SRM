package com.secure_srm.repositories.security;

import com.secure_srm.model.security.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByFirstNameAndLastName(String firstName, String lastName);

    Set<AdminUser> findByFirstNameLikeAndLastNameLike(String firstName, String lastName);

    Set<AdminUser> findAllByLastNameLike(String lastName);

    Set<AdminUser> findAllByFirstNameAndLastName(String firstName, String lastName);
}
