package com.secure_srm.converters;

import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.services.academicServices.AssignmentTypeService;
import com.secure_srm.services.securityServices.GuardianUserService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

//handles String id's to GuardianUser entities

@Component
@RequiredArgsConstructor
public class StringIDToAssignmentType implements Converter<String, AssignmentType> {

    private final AssignmentTypeService assignmentTypeService;

    @Synchronized
    @Nullable
    @Override
    public AssignmentType convert(String id) {
        return assignmentTypeService.findById(Long.valueOf(id));
    }
}
