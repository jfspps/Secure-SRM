package com.secure_srm.repositories.academicRepos;


import com.secure_srm.model.academic.StudentWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//the implementation of the following methods is supplied automatically by JPA

//declares additional SpringDataJPA CRUD functionality for StudentWork
public interface StudentWorkRepository extends JpaRepository<StudentWork, Long> {

    Optional<StudentWork> findByTitle(String title);

    Optional<StudentWork> findByTeacherUploader_LastName(String uploaderLastName);

    Optional<StudentWork> findByTeacherUploader_FirstNameAndTeacherUploader_LastName(String firstName, String lastName);

    Optional<StudentWork> findBySubject_SubjectName(String subjectName);

    Optional<StudentWork> findByAssignmentType_Description(String description);

    Optional<StudentWork> findByContributor(boolean isAContributor);
}
