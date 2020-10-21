package com.secure_srm.converters;

import com.secure_srm.model.academic.Threshold;
import com.secure_srm.model.people.Student;
import com.secure_srm.services.academicServices.ThresholdService;
import com.secure_srm.services.peopleServices.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

//handles String id's to Threshold entities

@Component
@RequiredArgsConstructor
public class StringIDToThreshold implements Converter<String, Threshold> {

    private final ThresholdService thresholdService;

    @Synchronized
    @Nullable
    @Override
    public Threshold convert(String id) {
        return thresholdService.findById(Long.valueOf(id));
    }
}
