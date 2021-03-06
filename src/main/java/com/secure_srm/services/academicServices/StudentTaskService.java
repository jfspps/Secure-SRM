package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.services.BaseService;

import java.util.Set;

public interface StudentTaskService extends BaseService<StudentTask, Long> {
    StudentTask findByTitle(String title);

    Set<StudentTask> findAllByTitleIgnoreCase(String title);

    StudentTask findByTitleAndTeacherUploaderId(String title, Long id);

    StudentTask findByTeacherLastName(String lastName);

    StudentTask findByTeacherFirstAndLastName(String firstName, String lastName);

    Set<StudentTask> findAllBySubject(String subjectName);

    StudentTask findByDescription(String description);

    StudentTask findByContribution(boolean isAContributor);
}
