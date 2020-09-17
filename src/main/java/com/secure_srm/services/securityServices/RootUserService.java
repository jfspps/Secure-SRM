package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.RootUser;

import java.util.Set;

public interface RootUserService extends BaseService<RootUser, Long> {

    RootUser findByFirstNameAndLastName(String firstName, String lastName);

    Set<RootUser> findByFirstNameLikeAndLastNameLike(String firstName, String lastName);

    Set<RootUser> findAllByLastNameLike(String lastName);

    Set<RootUser> findAllByFirstNameAndLastName(String firstName, String lastName);
}
