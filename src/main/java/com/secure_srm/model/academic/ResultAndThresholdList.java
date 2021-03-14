package com.secure_srm.model.academic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@AllArgsConstructor
@Getter
@Builder
public class ResultAndThresholdList {
    String gradeAwarded;
    ThresholdList thresholdList;
}
