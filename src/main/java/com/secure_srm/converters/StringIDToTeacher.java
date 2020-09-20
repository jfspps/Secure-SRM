package com.secure_srm.converters;

import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.securityServices.GuardianUserService;
import com.secure_srm.services.securityServices.TeacherUserService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

//handles String id's to TeacherUser entities

@Component
@RequiredArgsConstructor
public class StringIDToTeacher implements Converter<String, TeacherUser> {

    private final TeacherUserService teacherUserService;

    @Synchronized
    @Nullable
    @Override
    public TeacherUser convert(String id) {
        return teacherUserService.findById(Long.valueOf(id));
    }
}
