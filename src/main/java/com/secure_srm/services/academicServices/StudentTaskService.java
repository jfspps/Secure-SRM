package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.services.BaseService;

public interface StudentTaskService extends BaseService<StudentTask, Long> {
    StudentTask findByTitle(String title);

    StudentTask findByTitleAndTeacherUploaderId(String title, Long id);

    StudentTask findByTeacherLastName(String lastName);

    StudentTask findByTeacherFirstAndLastName(String firstName, String lastName);

    StudentTask findBySubject(String subjectName);

    StudentTask findByDescription(String description);

    StudentTask findByContribution(boolean isAContributor);
}
