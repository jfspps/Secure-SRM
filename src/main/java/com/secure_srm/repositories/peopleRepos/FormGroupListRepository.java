package com.secure_srm.repositories.peopleRepos;

import com.secure_srm.model.people.FormGroupList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//the implementation of the following methods is supplied automatically by JPA

//declares additional SpringDataJPA CRUD functionality for FormGroupList
public interface FormGroupListRepository extends JpaRepository<FormGroupList, Long> {

    Optional<FormGroupList> findByGroupName(String groupName);

    Optional<FormGroupList> findByTeacher_LastName(String teacherLastName);

    Optional<FormGroupList> findByTeacher_FirstNameAndTeacher_LastName(String firstName, String lastName);
}
