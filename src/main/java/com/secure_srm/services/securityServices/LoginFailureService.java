package com.secure_srm.services.securityServices;

import com.secure_srm.model.security.LoginFailure;
import com.secure_srm.model.security.User;

import java.sql.Timestamp;
import java.util.List;

public interface LoginFailureService extends BaseService<LoginFailure, Long> {

    //handle lockout
    List<LoginFailure> findAllByUserAndCreatedDateIsAfter(User user, Timestamp timestamp);
}
