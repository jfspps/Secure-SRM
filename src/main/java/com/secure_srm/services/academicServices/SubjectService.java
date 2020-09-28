package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.Subject;
import com.secure_srm.services.BaseService;

import java.util.Optional;
import java.util.Set;

public interface SubjectService extends BaseService<Subject, Long> {
    Subject findBySubjectName(String subjectName);

    Set<Subject> findBySubjectNameContainingIgnoreCase(String subjectTitle);
}
