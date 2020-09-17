package com.secure_srm.repositories.peopleRepos;

import com.secure_srm.model.people.SubjectClassList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//the implementation of the following methods is supplied automatically by JPA

//declares additional SpringDataJPA CRUD functionality for SubjectClassList
public interface SubjectClassListRepository extends JpaRepository<SubjectClassList, Long> {

    Optional<SubjectClassList> findBySubject_SubjectName(String subject);

    Optional<SubjectClassList> findByTeacher_LastName(String teacherLastName);

    Optional<SubjectClassList> findByGroupName(String groupName);

    Optional<SubjectClassList> findByTeacher_FirstNameAndTeacher_LastName(String firstName, String lastName);
}
