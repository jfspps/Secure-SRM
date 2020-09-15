package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.TeacherUser;

import java.util.Set;

public interface TeacherUserService extends BaseService<TeacherUser, Long>{
    TeacherUser findByTeacherUserName(String username);

    Set<TeacherUser> findAllByTeacherUserName(String userName);
}
