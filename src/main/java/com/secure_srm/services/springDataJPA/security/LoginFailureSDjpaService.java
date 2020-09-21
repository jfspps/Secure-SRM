package com.secure_srm.services.springDataJPA.security;

import com.secure_srm.model.security.LoginFailure;
import com.secure_srm.model.security.User;
import com.secure_srm.repositories.security.LoginFailureRepository;
import com.secure_srm.services.securityServices.LoginFailureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class LoginFailureSDjpaService implements LoginFailureService {

    private final LoginFailureRepository loginFailureRepository;

    public LoginFailureSDjpaService(LoginFailureRepository loginFailureRepository) {
        this.loginFailureRepository = loginFailureRepository;
    }

    @Override
    public LoginFailure save(LoginFailure object) {
        return loginFailureRepository.save(object);
    }

    @Override
    public LoginFailure findById(Long aLong) {
        return loginFailureRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<LoginFailure> findAll() {
        Set<LoginFailure> loginFailures = new HashSet<>();
        loginFailures.addAll(loginFailureRepository.findAll());
        return loginFailures;
    }

    @Override
    public List<LoginFailure> findAllByUserAndCreatedDateIsAfter(User user, Timestamp timestamp) {
        return loginFailureRepository.findAllByUserAndCreatedDateIsAfterOrderById(user, timestamp);
    }

    @Override
    public void delete(LoginFailure objectT) {
        loginFailureRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        loginFailureRepository.deleteById(aLong);
    }
}
