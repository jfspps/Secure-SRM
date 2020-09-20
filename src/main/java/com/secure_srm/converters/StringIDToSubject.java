package com.secure_srm.converters;

import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.securityServices.GuardianUserService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

//handles String id's to Subject entity

@Component
@RequiredArgsConstructor
public class StringIDToSubject implements Converter<String, Subject> {

    private final SubjectService subjectService;

    @Synchronized
    @Nullable
    @Override
    public Subject convert(String id) {
        return subjectService.findById(Long.valueOf(id));
    }
}
