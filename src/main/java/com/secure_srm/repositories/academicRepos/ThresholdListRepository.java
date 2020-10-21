package com.secure_srm.repositories.academicRepos;

import com.secure_srm.model.academic.ThresholdList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

//the implementation of the following methods is supplied automatically by JPA

//declares additional SpringDataJPA CRUD functionality for ThresholdList
public interface ThresholdListRepository extends JpaRepository<ThresholdList, Long> {

    Optional<ThresholdList> findByUniqueID(String uniqueID);

    Set<ThresholdList> findAllByUniqueIDContainingIgnoreCase(String uniqueID);

}
