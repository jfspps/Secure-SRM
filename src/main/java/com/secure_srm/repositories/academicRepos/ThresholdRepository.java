package com.secure_srm.repositories.academicRepos;

import com.secure_srm.model.academic.Threshold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;


//the implementation of the following methods is supplied automatically by JPA

//declares additional SpringDataJPA CRUD functionality for Threshold
public interface ThresholdRepository extends JpaRepository<Threshold, Long> {

    Optional<Threshold> findByNumerical(int numericalBoundary);

    Optional<Threshold> findByAlphabetical(String letterGrade);

    Optional<Threshold> findByUniqueId(String uniqueID);

    Set<Threshold> findAllByUniqueId(String uniqueID);

    Set<Threshold> findAllByUploader_LastName(String lastName);

    Set<Threshold> findAllByUniqueIdContainingIgnoreCase(String uniqueID);
}
