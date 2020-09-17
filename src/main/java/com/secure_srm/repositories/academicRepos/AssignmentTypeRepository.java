package com.secure_srm.repositories.academicRepos;

import com.secure_srm.model.academic.AssignmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//the implementation of the following methods is supplied automatically by JPA

//declares additional SpringDataJPA CRUD functionality for AssignmentType
public interface AssignmentTypeRepository extends JpaRepository<AssignmentType, Long> {

    Optional<AssignmentType> findByDescription(String description);
}
