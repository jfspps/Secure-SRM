package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.GuardianUser;

import java.util.Set;

public interface GuardianUserService extends BaseService<GuardianUser, Long>{
    GuardianUser findByGuardianUserName(String username);

    Set<GuardianUser> findAllByGuardianUserName(String userName);
}
