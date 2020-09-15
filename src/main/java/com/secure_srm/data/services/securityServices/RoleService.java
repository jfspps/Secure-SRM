package com.secure_srm.data.services.securityServices;

import com.secure_srm.data.model.security.Role;

public interface RoleService extends BaseService<Role, Long> {
    // declare custom (map-related) query methods here
    Role findByRoleName(String roleName);
}
