package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.model.academic.StudentResult;
import com.secure_srm.repositories.academicRepos.AssignmentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//see StudentSDjpaServiceTest for commentary
@ExtendWith(MockitoExtension.class)
class AssignmentTypeSDjpaServiceTest {

    final String description1 = "Exam";
    final String description3 = "Coursework";

    StudentResult studentResult = new StudentResult();
    Set<StudentResult> studentResults = new HashSet<>();

    AssignmentType exams;
    AssignmentType coursework;

    @Mock
    AssignmentTypeRepository assignmentTypeRepository;

    @InjectMocks
    AssignmentTypeSDjpaService assignmentTypeSDjpaService;

    @BeforeEach
    void setUp() {
        studentResults.add(studentResult);
        exams = AssignmentType.builder().description(description1).studentResults(studentResults).build();
        coursework = AssignmentType.builder().description(description3).studentResults(studentResults).build();
    }

    @Test
    void findByDescription() {
        when(assignmentTypeRepository.findByDescription(any())).thenReturn(Optional.of(exams));

        AssignmentType found = assignmentTypeSDjpaService.findByDescription("test");
        verify(assignmentTypeRepository, times(1)).findByDescription(any());
        assertEquals(description1, found.getDescription());
    }

    @Test
    void findByDescriptionCaseSensitive() {
        AssignmentType found = assignmentTypeSDjpaService.findByDescription("exam");
        assertNull(found);
    }

    @Test
    void findAll() {
        assignmentTypeSDjpaService.save(exams);
        assignmentTypeSDjpaService.save(coursework);

        Set<AssignmentType> assignmentTypes = assignmentTypeSDjpaService.findAll();
        assertEquals(0, assignmentTypes.size());
        //save() not persisting at the moment...
    }
}