package com.secure_srm.data.repositories.security;

import com.secure_srm.data.model.security.LoginSuccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginSuccessRepository extends JpaRepository<LoginSuccess, Long> {
}
