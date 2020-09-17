package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.Role;
import com.secure_srm.services.BaseService;

public interface RoleService extends BaseService<Role, Long> {
    // declare custom (map-related) query methods here
    Role findByRoleName(String roleName);
}
