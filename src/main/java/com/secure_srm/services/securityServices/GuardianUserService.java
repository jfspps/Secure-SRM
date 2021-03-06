package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.services.BaseService;

import java.util.Set;

public interface GuardianUserService extends BaseService<GuardianUser, Long> {

    GuardianUser findByFirstNameAndLastName(String firstName, String lastName);

    Set<GuardianUser> findByFirstNameLikeAndLastNameLike(String firstName, String lastName);

    Set<GuardianUser> findAllByLastNameLike(String lastName);

    Set<GuardianUser> findAllByLastNameContainingIgnoreCase(String lastName);

    Set<GuardianUser> findAllByFirstNameAndLastName(String firstName, String lastName);
}
