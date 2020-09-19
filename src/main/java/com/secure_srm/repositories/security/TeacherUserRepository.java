package com.secure_srm.repositories.security;

import com.secure_srm.model.security.TeacherUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TeacherUserRepository extends JpaRepository<TeacherUser, Long> {

    Optional<TeacherUser> findByFirstNameAndLastName(String firstName, String lastName);

    Set<TeacherUser> findByFirstNameLikeAndLastNameLike(String firstName, String lastName);

    Set<TeacherUser> findAllByLastNameLike(String lastName);

    Set<TeacherUser> findAllByLastNameContainingIgnoreCase(String lastName);

    Set<TeacherUser> findAllByFirstNameAndLastName(String firstName, String lastName);
}
