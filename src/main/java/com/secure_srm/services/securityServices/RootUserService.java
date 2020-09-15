package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.RootUser;

public interface RootUserService extends BaseService<RootUser, Long> {
    RootUser findByRootUserName(String username);
}
