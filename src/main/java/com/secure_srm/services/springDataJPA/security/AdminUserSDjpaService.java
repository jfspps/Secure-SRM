package com.secure_srm.services.springDataJPA.security;

import com.secure_srm.model.security.AdminUser;
import com.secure_srm.repositories.security.AdminUserRepository;
import com.secure_srm.services.securityServices.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class AdminUserSDjpaService implements AdminUserService {

    private final AdminUserRepository adminUserRepository;

    public AdminUserSDjpaService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public AdminUser save(AdminUser object) {
        return adminUserRepository.save(object);
    }

    @Override
    public AdminUser findById(Long aLong) {
        return adminUserRepository.findById(aLong).orElse(null);
    }

    @Override
    public AdminUser findByFirstNameAndLastName(String firstName, String lastName) {
        return adminUserRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);
    }

    @Override
    public Set<AdminUser> findByFirstNameLikeAndLastNameLike(String firstName, String lastName) {
        return adminUserRepository.findByFirstNameLikeAndLastNameLike(firstName, lastName);
    }

    @Override
    public Set<AdminUser> findAllByLastNameLike(String lastName) {
        return adminUserRepository.findAllByLastNameLike(lastName);
    }

    @Override
    public Set<AdminUser> findAllByFirstNameAndLastName(String firstName, String lastName) {
        return adminUserRepository.findAllByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    public Set<AdminUser> findAll() {
        Set<AdminUser> adminUsers = new HashSet<>();
        adminUsers.addAll(adminUserRepository.findAll());
        return adminUsers;
    }

    @Override
    public void delete(AdminUser objectT) {
        adminUserRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        adminUserRepository.deleteById(aLong);
    }
}
