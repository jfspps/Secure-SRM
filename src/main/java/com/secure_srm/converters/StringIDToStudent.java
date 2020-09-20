package com.secure_srm.converters;

import com.secure_srm.model.people.Student;
import com.secure_srm.services.peopleServices.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

//handles String id's to Student entities

@Component
@RequiredArgsConstructor
public class StringIDToStudent implements Converter<String, Student> {

    private final StudentService studentService;

    @Synchronized
    @Nullable
    @Override
    public Student convert(String id) {
        return studentService.findById(Long.valueOf(id));
    }
}
