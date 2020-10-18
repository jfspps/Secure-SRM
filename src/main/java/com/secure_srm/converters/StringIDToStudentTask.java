package com.secure_srm.converters;

import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.services.academicServices.AssignmentTypeService;
import com.secure_srm.services.academicServices.StudentTaskService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

//handles String id's to StudentTask entities

@Component
@RequiredArgsConstructor
public class StringIDToStudentTask implements Converter<String, StudentTask> {

    private final StudentTaskService studentTaskService;

    @Synchronized
    @Nullable
    @Override
    public StudentTask convert(String id) {
        return studentTaskService.findById(Long.valueOf(id));
    }
}
