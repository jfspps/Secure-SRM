package com.secure_srm.data.services.springDataJPA.security;

import com.secure_srm.data.model.security.TeacherUser;
import com.secure_srm.data.repositories.security.TeacherUserRepository;
import com.secure_srm.data.services.securityServices.TeacherUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class TeacherUserSDjpaService implements TeacherUserService {

    private final TeacherUserRepository teacherUserRepository;

    public TeacherUserSDjpaService(TeacherUserRepository adminUserRepository) {
        this.teacherUserRepository = adminUserRepository;
    }

    @Override
    public TeacherUser save(TeacherUser object) {
        return teacherUserRepository.save(object);
    }

    @Override
    public TeacherUser findById(Long aLong) {
        return teacherUserRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<TeacherUser> findAll() {
        Set<TeacherUser> adminUsers = new HashSet<>();
        adminUsers.addAll(teacherUserRepository.findAll());
        return adminUsers;
    }

    @Override
    public TeacherUser findByTeacherUserName(String username) {
        return teacherUserRepository.findByTeacherUserName(username).orElse(null);
    }

    @Override
    public Set<TeacherUser> findAllByTeacherUserName(String userName) {
        return teacherUserRepository.findAllByTeacherUserName(userName);
    }

    @Override
    public void delete(TeacherUser objectT) {
        teacherUserRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        teacherUserRepository.deleteById(aLong);
    }
}
