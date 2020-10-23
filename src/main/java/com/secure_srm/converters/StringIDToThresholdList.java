package com.secure_srm.converters;

import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.model.academic.ThresholdList;
import com.secure_srm.services.academicServices.StudentTaskService;
import com.secure_srm.services.academicServices.ThresholdListService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

//handles String id's to StudentTask entities

@Component
@RequiredArgsConstructor
public class StringIDToThresholdList implements Converter<String, ThresholdList> {

    private final ThresholdListService thresholdListService;

    @Synchronized
    @Nullable
    @Override
    public ThresholdList convert(String id) {
        return thresholdListService.findById(Long.valueOf(id));
    }
}
