package com.secure_srm.repositories.academicRepos;

import com.secure_srm.model.academic.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//the implementation of the following methods is supplied automatically by JPA

//declares additional SpringDataJPA CRUD functionality for Subjects
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findBySubjectName(String subjectName);
}
