package com.secure_srm.services.peopleServices;

import com.secure_srm.model.people.Student;
import com.secure_srm.services.BaseService;

import java.util.List;
import java.util.Set;

public interface StudentService extends BaseService<Student, Long> {

    //other methods not declared in CrudService

    Student findByLastName(String lastName);

    Student findByFirstAndLastName(String firstName, String lastName);

    Student findByFirstLastAndMiddleNames(String firstName, String lastName, String middleNames);

    Set<Student> findAllByLastNameLike(String lastName);

    Set<Student> findAllByLastNameContainingIgnoreCase(String lastName);

    Set<Student> findAllByFirstNameLikeAndLastNameLike(String firstName, String lastName);

    List<Student> findAllByOrderByLastName();
}
