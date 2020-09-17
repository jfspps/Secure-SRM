package com.secure_srm.services.peopleServices;

import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.services.BaseService;

public interface FormGroupListService extends BaseService<FormGroupList, Long> {
    FormGroupList findByGroupName(String groupName);

    FormGroupList findByTeacherLastName(String lastName);

    FormGroupList findByTeacherFirstAndLastName(String firstName, String lastName);
}
