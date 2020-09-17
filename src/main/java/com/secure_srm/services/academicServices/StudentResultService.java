package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.StudentResult;
import com.secure_srm.services.BaseService;

public interface StudentResultService extends BaseService<StudentResult, Long> {
    StudentResult findByStudentLastName(String lastName);

    StudentResult findByStudentFirstAndLastName(String firstName, String lastName);

    StudentResult findByTeacherLastName(String lastName);

    StudentResult findByTeacherFirstAndLastName(String firstName, String lastName);

    StudentResult findByStudentWorkTitle(String assignmentTitle);

    StudentResult findByScore(String score);
}
