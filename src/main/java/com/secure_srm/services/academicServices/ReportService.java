package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.Report;
import com.secure_srm.services.BaseService;

public interface ReportService extends BaseService<Report, Long> {
    Report findByStudentLastName(String lastName);

    Report findByStudentFirstAndLastName(String firstName, String lastName);

    Report findByTeacherLastName(String lastName);

    Report findByTeacherFirstAndLastName(String firstName, String lastName);

    Report findBySubject(String subjectName);
}
