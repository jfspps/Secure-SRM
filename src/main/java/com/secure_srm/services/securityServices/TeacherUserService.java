package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.BaseService;

import java.util.Set;

public interface TeacherUserService extends BaseService<TeacherUser, Long> {

    TeacherUser findByFirstNameAndLastName(String firstName, String lastName);

    Set<TeacherUser> findByFirstNameLikeAndLastNameLike(String firstName, String lastName);

    Set<TeacherUser> findAllByLastNameLike(String lastName);

    Set<TeacherUser> findAllByFirstNameAndLastName(String firstName, String lastName);
}
