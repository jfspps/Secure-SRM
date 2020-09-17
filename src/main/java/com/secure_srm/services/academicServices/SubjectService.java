package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.Subject;
import com.secure_srm.services.securityServices.BaseService;

public interface SubjectService extends BaseService<Subject, Long> {
    Subject findBySubjectName(String subjectName);
}
