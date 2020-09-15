package com.secure_srm.data.services.securityServices;

import com.secure_srm.data.model.security.RootUser;

public interface RootUserService extends BaseService<RootUser, Long> {
    RootUser findByRootUserName(String username);
}
