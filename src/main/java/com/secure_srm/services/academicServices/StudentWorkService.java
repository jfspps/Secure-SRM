package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.StudentWork;
import com.secure_srm.services.BaseService;

public interface StudentWorkService extends BaseService<StudentWork, Long> {
    StudentWork findByTitle(String title);

    StudentWork findByTeacherLastName(String lastName);

    StudentWork findByTeacherFirstAndLastName(String firstName, String lastName);

    StudentWork findBySubject(String subjectName);

    StudentWork findByDescription(String description);

    StudentWork findByContribution(boolean isAContributor);
}
