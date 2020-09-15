package com.secure_srm.services.springDataJPA.security;

import com.secure_srm.model.security.RootUser;
import com.secure_srm.repositories.security.RootUserRepository;
import com.secure_srm.services.securityServices.RootUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class RootUserSDjpaService implements RootUserService {

    private final RootUserRepository rootUserRepository;

    public RootUserSDjpaService(RootUserRepository rootUserRepository) {
        this.rootUserRepository = rootUserRepository;
    }

    @Override
    public RootUser save(RootUser object) {
        return rootUserRepository.save(object);
    }

    @Override
    public RootUser findById(Long aLong) {
        return rootUserRepository.findById(aLong).orElse(null);
    }

    @Override
    public RootUser findByRootUserName(String username) {
        return rootUserRepository.findByRootUserName(username).orElse(null);
    }

    @Override
    public Set<RootUser> findAll() {
        Set<RootUser> rootUsers = new HashSet<>();
        rootUsers.addAll(rootUserRepository.findAll());
        return rootUsers;
    }

    @Override
    public void delete(RootUser objectT) {
        rootUserRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        rootUserRepository.deleteById(aLong);
    }
}
