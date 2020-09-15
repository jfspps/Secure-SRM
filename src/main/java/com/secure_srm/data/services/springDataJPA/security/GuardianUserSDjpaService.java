package com.secure_srm.data.services.springDataJPA.security;

import com.secure_srm.data.model.security.GuardianUser;
import com.secure_srm.data.repositories.security.GuardianUserRepository;
import com.secure_srm.data.services.securityServices.GuardianUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class GuardianUserSDjpaService implements GuardianUserService {

    private final GuardianUserRepository guardianUserRepository;

    public GuardianUserSDjpaService(GuardianUserRepository guardianUserRepository) {
        this.guardianUserRepository = guardianUserRepository;
    }

    @Override
    public GuardianUser save(GuardianUser object) {
        return guardianUserRepository.save(object);
    }

    @Override
    public GuardianUser findById(Long aLong) {
        return guardianUserRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<GuardianUser> findAll() {
        Set<GuardianUser> adminUsers = new HashSet<>();
        adminUsers.addAll(guardianUserRepository.findAll());
        return adminUsers;
    }

    @Override
    public GuardianUser findByGuardianUserName(String username) {
        return guardianUserRepository.findByGuardianUserName(username).orElse(null);
    }

    @Override
    public Set<GuardianUser> findAllByGuardianUserName(String userName) {
        return guardianUserRepository.findAllByGuardianUserName(userName);
    }

    @Override
    public void delete(GuardianUser objectT) {
        guardianUserRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        guardianUserRepository.deleteById(aLong);
    }
}
