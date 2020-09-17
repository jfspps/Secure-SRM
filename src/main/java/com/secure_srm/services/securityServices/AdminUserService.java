package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.AdminUser;

import java.util.Optional;
import java.util.Set;

public interface AdminUserService extends BaseService<AdminUser, Long> {
    AdminUser findByFirstNameAndLastName(String firstName, String lastName);

    Set<AdminUser> findByFirstNameLikeAndLastNameLike(String firstName, String lastName);

    Set<AdminUser> findAllByLastNameLike(String lastName);

    Set<AdminUser> findAllByFirstNameAndLastName(String firstName, String lastName);
}
