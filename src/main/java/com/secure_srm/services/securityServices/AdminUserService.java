package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.AdminUser;

import java.util.Set;

public interface AdminUserService extends BaseService<AdminUser, Long> {
    AdminUser findByAdminUserName(String username);

    Set<AdminUser> findAllByAdminUserName(String userName);
}
