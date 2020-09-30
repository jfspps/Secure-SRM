package com.secure_srm.converters;

import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.services.peopleServices.FormGroupListService;
import com.secure_srm.services.securityServices.GuardianUserService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

//handles String id's to FormGroupList entities

@Component
@RequiredArgsConstructor
public class StringIDToFormGroupList implements Converter<String, FormGroupList> {

    private final FormGroupListService formGroupListService;

    @Synchronized
    @Nullable
    @Override
    public FormGroupList convert(String id) {
        return formGroupListService.findById(Long.valueOf(id));
    }
}
