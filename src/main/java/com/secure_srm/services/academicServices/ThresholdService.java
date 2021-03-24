package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.Threshold;
import com.secure_srm.services.BaseService;

import java.util.Set;

public interface ThresholdService extends BaseService<Threshold, Long> {
    Threshold findByNumericalBoundary(int numericalBoundary);

    Threshold findByLetterGrade(String letterGrade);

    Set<Threshold> findAllByUploaderLastName(String lastName);

    Set<Threshold> findAllByUniqueIDContainingIgnoreCase(String uniqueID);

    Threshold findByUniqueID(String uniqueID);

    Set<Threshold> findAllByUniqueID(String uniqueID);

    Set<Threshold> findAllByTeacher(String firstName, String lastName);
}
