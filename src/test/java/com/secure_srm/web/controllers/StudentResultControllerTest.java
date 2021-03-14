package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StudentResultControllerTest {

    @InjectMocks
    StudentResultController studentResultController;

    StudentResult studentResultOneThresholdList;
    StudentResult studentResultNoThresholdLists;
    StudentResult studentResultMixedThresholdLists;

    StudentTask studentTaskOneThresholdList;
    StudentTask studentTaskNoThresholdLists;
    StudentTask studentTaskMultipleThresholdLists;

    ThresholdList thresholdList;
    ThresholdList emptyThresholdList;

    private final Threshold threshold1 = Threshold.builder().numerical(90).alphabetical("A").build();
    private final Threshold threshold2 = Threshold.builder().numerical(95).alphabetical("A+").build();

    @BeforeEach
    void setUp() {
        thresholdList = ThresholdList.builder().uniqueID("ThresholdA_A+").thresholds(Set.of(threshold1, threshold2)).build();
        emptyThresholdList = ThresholdList.builder().uniqueID("Threshold_empty").build();

        studentTaskOneThresholdList = StudentTask.builder().thresholdListSet(Set.of(thresholdList)).build();
        studentTaskNoThresholdLists = StudentTask.builder().thresholdListSet(Set.of(emptyThresholdList)).build();
        studentTaskMultipleThresholdLists = StudentTask.builder().thresholdListSet(Set.of(thresholdList, emptyThresholdList)).build();
    }

    @Test
    void getResultAndThresholdsAboveThreshold() {
        String gradeExpected = "A+";
        String numericalScore = "96";
        studentResultOneThresholdList = StudentResult.builder().score(numericalScore).studentTask(studentTaskOneThresholdList).build();

        Set<ResultAndThresholdList> resultAndThresholdLists = studentResultController.getResultAndThresholdLists(studentResultOneThresholdList);

        String gradeFound = resultAndThresholdLists.stream().findAny().get().getGradeAwarded();

        assertEquals(gradeExpected, gradeFound);
    }

    @Test
    void getResultAndThresholdsAtThreshold() {
        String gradeExpected = "A+";
        String numericalScore = "95";
        studentResultOneThresholdList = StudentResult.builder().score(numericalScore).studentTask(studentTaskOneThresholdList).build();

        Set<ResultAndThresholdList> resultAndThresholdLists = studentResultController.getResultAndThresholdLists(studentResultOneThresholdList);

        String gradeFound = resultAndThresholdLists.stream().findAny().get().getGradeAwarded();

        assertEquals(gradeExpected, gradeFound);
    }

    @Test
    void getResultAndThresholds_belowAllThresholds() {
        String gradeExpected = "No grade available";
        String numericalScore = "15";
        studentResultOneThresholdList = StudentResult.builder().score(numericalScore).studentTask(studentTaskOneThresholdList).build();

        Set<ResultAndThresholdList> resultAndThresholdLists = studentResultController.getResultAndThresholdLists(studentResultOneThresholdList);

        String gradeFound = resultAndThresholdLists.stream().findAny().get().getGradeAwarded();

        assertEquals(gradeExpected, gradeFound);
    }

    @Test
    void getResultAndThresholds_EmptyLists() {
        String numericalScore = "96";
        studentResultNoThresholdLists = StudentResult.builder().score(numericalScore).studentTask(studentTaskNoThresholdLists).build();

        Set<ResultAndThresholdList> resultAndThresholdLists = studentResultController.getResultAndThresholdLists(studentResultNoThresholdLists);
        ResultAndThresholdList resultAndThresholdList = resultAndThresholdLists.stream().findAny().orElse(null);

        assertNull(resultAndThresholdList);
    }

    @Test
    void getResultAndThresholds_MultipleThresholdLists() {
        String gradeExpected = "A+";
        String numericalScore = "96";
        studentResultMixedThresholdLists = StudentResult.builder().score(numericalScore).studentTask(studentTaskMultipleThresholdLists).build();

        Set<ResultAndThresholdList> resultAndThresholdLists = studentResultController.getResultAndThresholdLists(studentResultMixedThresholdLists);
        String gradeFound1 = resultAndThresholdLists.stream().filter(resultAndThresholdList ->
                resultAndThresholdList.getThresholdList().getUniqueID().equals("ThresholdA_A+")).findFirst().get().getGradeAwarded();

        ResultAndThresholdList resultThreshListFound = resultAndThresholdLists.stream().filter(resultAndThresholdList ->
                resultAndThresholdList.getThresholdList().getUniqueID().equals("Threshold_empty")).findFirst().orElse(null);

        assertEquals(gradeExpected, gradeFound1);
        assertNull(resultThreshListFound);
    }
}