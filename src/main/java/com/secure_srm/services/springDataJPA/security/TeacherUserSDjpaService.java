package com.secure_srm.services.springDataJPA.security;

import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.repositories.security.TeacherUserRepository;
import com.secure_srm.services.securityServices.TeacherUserService;
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
    public TeacherUser findByFirstNameAndLastName(String firstName, String lastName) {
        return teacherUserRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);
    }

    @Override
    public Set<TeacherUser> findByFirstNameLikeAndLastNameLike(String firstName, String lastName) {
        return teacherUserRepository.findByFirstNameLikeAndLastNameLike(firstName, lastName);
    }

    @Override
    public Set<TeacherUser> findAllByLastNameContainingIgnoreCase(String lastName) {
        return teacherUserRepository.findAllByLastNameContainingIgnoreCase(lastName);
    }

    @Override
    public Set<TeacherUser> findAllByLastNameLike(String lastName) {
        return teacherUserRepository.findAllByLastNameLike(lastName);
    }

    @Override
    public Set<TeacherUser> findAllByFirstNameAndLastName(String firstName, String lastName) {
        return teacherUserRepository.findAllByFirstNameAndLastName(firstName, lastName);
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
