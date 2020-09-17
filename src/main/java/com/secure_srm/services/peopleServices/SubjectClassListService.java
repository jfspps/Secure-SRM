package com.secure_srm.services.peopleServices;

import com.secure_srm.model.people.SubjectClassList;
import com.secure_srm.services.BaseService;

public interface SubjectClassListService extends BaseService<SubjectClassList, Long> {
    SubjectClassList findBySubject(String subjectName);

    SubjectClassList findByTeacherLastName(String lastName);

    SubjectClassList findByGroupName(String groupName);

    SubjectClassList findByTeacherFirstAndLastName(String firstName, String lastName);
}
