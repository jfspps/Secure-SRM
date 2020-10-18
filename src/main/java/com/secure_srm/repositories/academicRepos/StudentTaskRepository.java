package com.secure_srm.repositories.academicRepos;


import com.secure_srm.model.academic.StudentTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

//the implementation of the following methods is supplied automatically by JPA

//declares additional SpringDataJPA CRUD functionality for StudentWork
public interface StudentTaskRepository extends JpaRepository<StudentTask, Long> {

    Optional<StudentTask> findByTitle(String title);

    Set<StudentTask> findAllByTitleContainingIgnoreCase(String title);

    Optional<StudentTask> findByTitleAndTeacherUploader_Id(String title, Long id);

    Optional<StudentTask> findByTeacherUploader_LastName(String uploaderLastName);

    Optional<StudentTask> findByTeacherUploader_FirstNameAndTeacherUploader_LastName(String firstName, String lastName);

    Set<StudentTask> findAllBySubject_SubjectName(String subjectName);

    Optional<StudentTask> findByAssignmentType_Description(String description);

    Optional<StudentTask> findByContributor(boolean isAContributor);
}
