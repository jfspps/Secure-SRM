package com.secure_srm.repositories.peopleRepos;

import com.secure_srm.model.people.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

// The repository is an interface of JPA based methods. The implementation of the methods below is supplied
// automatically by JPA

// The methods in /springDataJPA map the (auto-implemented) Spring Data JPA methods below to the methods listed in the
// respective Services (under /academicServices and /peopleServices). The Spring Data JPA method names can differ from
// the method names listed in each Service. A good example to compare are the FormGroupList interfaces.

// The mapping allows one to call a Spring Data JPA method by a different name and also allows one to borrow the
// method names intended for other dependencies (like the HashSet implementation)

//declares additional SpringDataJPA CRUD functionality for Students
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByLastName(String lastName);

    Optional<Student> findByFirstNameAndLastName(String firstName, String lastName);

    Optional<Student> findByFirstNameAndLastNameAndMiddleNames(String firstName, String lastName, String middleNames);

    Set<Student> findAllByLastNameLike(String lastName);

    Set<Student> findAllByLastNameContainingIgnoreCase(String lastName);

    Set<Student> findAllByFirstNameLikeAndLastNameLike(String firstName, String lastName);
}
