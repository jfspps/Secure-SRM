package com.springsecurity.weblogin.bootstrap.security;

import com.springsecurity.weblogin.model.security.Authority;
import com.springsecurity.weblogin.model.security.User;
import com.springsecurity.weblogin.repositories.security.AuthorityRepository;
import com.springsecurity.weblogin.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("SDjpa")
@RequiredArgsConstructor
public class DataLoader_SDjpa implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder DBpasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        if (authorityRepository.findAll().size() == 0){
            populateH2();
            log.debug("Authority database finished populating");
        } else
            log.debug("Authority database already contains data; no changes made");
    }

    private void populateH2(){
        //example, as per Student Record Management (SRM) account
        Authority adminAuthority = authorityRepository.save(Authority.builder().role("ADMIN").build());
        Authority teacherAuthority = authorityRepository.save(Authority.builder().role("TEACHER").build());
        Authority guardianAuthority = authorityRepository.save(Authority.builder().role("GUARDIAN").build());
        log.debug("Authorities added: " + authorityRepository.findAll().size());

        userRepository.save(User.builder()
                .username("admin")
                .password(DBpasswordEncoder.encode("admin123"))
                .authority(adminAuthority)  //singular set, courtesy of Project Lombok
                .build());
        userRepository.save(User.builder()
                .username("user")
                .password(DBpasswordEncoder.encode("user123"))
                .authority(teacherAuthority)
                .build());
        userRepository.save(User.builder()
                .username("guardian1")
                .password(DBpasswordEncoder.encode("guardian123"))
                .authority(guardianAuthority)
                .build());
        userRepository.save(User.builder()
                .username("guardian2")
                .password(DBpasswordEncoder.encode("guardian456"))
                .authority(guardianAuthority)
                .build());
        log.debug("Users added: " + userRepository.findAll().size());
    }
}