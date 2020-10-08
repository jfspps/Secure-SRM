package com.secure_srm.repositories.academicRepos;


import com.secure_srm.model.academic.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

//the implementation of the following methods is supplied automatically by JPA

//declares additional SpringDataJPA CRUD functionality for Report
public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByStudent_LastName(String studentLastName);

    Optional<Report> findByStudent_FirstNameAndStudent_LastName(String firstName, String lastName);

    Optional<Report> findByTeacher_LastName(String teacherLastName);

    Optional<Report> findByTeacher_FirstNameAndTeacher_LastName(String firstName, String lastName);

    Optional<Report> findBySubject_SubjectName(String subjectName);

    Optional<Report> findByUniqueIdentifier(String uniqueIdentifier);
}
