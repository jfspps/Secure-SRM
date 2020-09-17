package com.secure_srm.repositories.security;

import com.secure_srm.model.security.RootUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RootUserRepository extends JpaRepository<RootUser, Long> {

    Optional<RootUser> findByFirstNameAndLastName(String firstName, String lastName);

    Set<RootUser> findByFirstNameLikeAndLastNameLike(String firstName, String lastName);

    Set<RootUser> findAllByLastNameLike(String lastName);

    Set<RootUser> findAllByFirstNameAndLastName(String firstName, String lastName);
}
