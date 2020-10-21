package com.secure_srm.services.academicServices;

import com.secure_srm.model.academic.ThresholdList;
import com.secure_srm.services.BaseService;

import java.util.Set;

public interface ThresholdListService extends BaseService<ThresholdList, Long> {

    ThresholdList findByUniqueID(String uniqueID);

    Set<ThresholdList> findAllByUniqueIDContainingIgnoreCase(String uniqueID);
}
