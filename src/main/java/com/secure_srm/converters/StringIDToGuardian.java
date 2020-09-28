package com.secure_srm.converters;

import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.services.securityServices.GuardianUserService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

//handles String id's to GuardianUser entities

@Component
@RequiredArgsConstructor
public class StringIDToGuardian implements Converter<String, GuardianUser> {

    private final GuardianUserService guardianUserService;

    @Synchronized
    @Nullable
    @Override
    public GuardianUser convert(String id) {
        return guardianUserService.findById(Long.valueOf(id));
    }
}
