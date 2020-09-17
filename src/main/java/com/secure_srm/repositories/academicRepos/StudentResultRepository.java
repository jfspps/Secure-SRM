package com.secure_srm.repositories.academicRepos;


//the implementation of the following methods is supplied automatically by JPA

import com.secure_srm.model.academic.StudentResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//declares additional SpringDataJPA CRUD functionality for StudentResult
public interface StudentResultRepository extends JpaRepository<StudentResult, Long> {

    Optional<StudentResult> findByStudent_LastName(String studentLastName);

    Optional<StudentResult> findByStudent_FirstNameAndStudent_LastName(String firstName, String lastName);

    Optional<StudentResult> findByTeacher_LastName(String markerLastName);

    Optional<StudentResult> findByTeacher_FirstNameAndTeacher_LastName(String firstName, String lastName);

    Optional<StudentResult> findByStudentWork_Title(String assignmentTitle);

    Optional<StudentResult> findByScore(String score);
}
