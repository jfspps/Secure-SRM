package com.secure_srm.repositories.security;

import com.secure_srm.model.security.GuardianUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface GuardianUserRepository extends JpaRepository<GuardianUser, Long>  {

    Optional<GuardianUser> findByFirstNameAndLastName(String firstName, String lastName);

    Set<GuardianUser> findByFirstNameLikeAndLastNameLike(String firstName, String lastName);

    Set<GuardianUser> findAllByLastNameLike(String lastName);

    Set<GuardianUser> findAllByFirstNameAndLastName(String firstName, String lastName);
}
