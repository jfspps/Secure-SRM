package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.services.securityServices.BaseService;

public interface AssignmentTypeService extends BaseService<AssignmentType, Long> {
    AssignmentType findByDescription(String description);
}
