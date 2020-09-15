package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.User;

import java.util.Set;

public interface UserService extends BaseService<User, Long> {
    // declare custom (map-related) query methods here
    User findByUsername(String username);

    Set<User> findAllByUsername(String username);
}
