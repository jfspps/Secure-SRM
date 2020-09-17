package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.Threshold;
import com.secure_srm.services.BaseService;

public interface ThresholdService extends BaseService<Threshold, Long> {
    Threshold findByNumericalBoundary(int numericalBoundary);

    Threshold findByLetterGrade(String letterGrade);
}
