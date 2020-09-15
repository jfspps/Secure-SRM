package com.secure_srm.data.repositories.security;

import com.secure_srm.data.model.security.TeacherUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TeacherUserRepository extends JpaRepository<TeacherUser, Long> {
    Optional<TeacherUser> findByTeacherUserName(String username);

    Set<TeacherUser> findAllByTeacherUserName(String userName);
}
