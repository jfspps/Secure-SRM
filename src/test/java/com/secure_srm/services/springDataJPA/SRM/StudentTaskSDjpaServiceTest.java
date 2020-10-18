package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.repositories.academicRepos.StudentTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//see StudentSDjpaServiceTest for commentary
@ExtendWith(MockitoExtension.class)
class StudentTaskSDjpaServiceTest {

    final String teacherLastName = "Jones";
    final String teacherFirstName = "Tom";

    //needed for StudentWork (contributor is set to true by default)
    TeacherUser setter = TeacherUser.builder().firstName(teacherFirstName).lastName(teacherLastName).build();
    final String subjectName = "English";
    Subject subject = Subject.builder().subjectName(subjectName).build();
    final String maxScore = "54";
    final String description = "End of term exam";
    AssignmentType assignmentType = AssignmentType.builder().description(description).build();
    final String assignmentTitle = "The merits of testing";

    StudentTask exam;

    @Mock
    StudentTaskRepository studentTaskRepository;

    @InjectMocks
    StudentTaskSDjpaService studentTaskSDjpaService;

    @BeforeEach
    void setUp() {
        exam = StudentTask.builder()
                .teacherUploader(setter)
                .subject(subject)
                .maxScore(maxScore)
                .contributor(true)
                .assignmentType(assignmentType)
                .title(assignmentTitle).build();
    }

    @Test
    void findByTitle() {
        when(studentTaskRepository.findByTitle(anyString())).thenReturn(Optional.of(exam));

        StudentTask found = studentTaskSDjpaService.findByTitle("The horrors of youth");
        verify(studentTaskRepository, times(1)).findByTitle(anyString());
        assertEquals(assignmentTitle, found.getTitle());
    }

    @Test
    void findByTeacherLastName() {
        when(studentTaskRepository.findByTeacherUploader_LastName(anyString())).thenReturn(Optional.of(exam));

        StudentTask found = studentTaskSDjpaService.findByTeacherLastName("Smith");
        verify(studentTaskRepository, times(1)).findByTeacherUploader_LastName(anyString());
        assertEquals(teacherLastName, found.getTeacherUploader().getLastName());
    }

    @Test
    void findByTeacherFirstAndLastName() {
        when(studentTaskRepository.findByTeacherUploader_FirstNameAndTeacherUploader_LastName(anyString(), anyString()))
                .thenReturn(Optional.of(exam));

        StudentTask found = studentTaskSDjpaService.findByTeacherFirstAndLastName("John", "Smith");
        verify(studentTaskRepository, times(1))
                .findByTeacherUploader_FirstNameAndTeacherUploader_LastName(anyString(), anyString());
        assertEquals(teacherLastName, found.getTeacherUploader().getLastName());
        assertEquals(teacherFirstName, found.getTeacherUploader().getFirstName());
    }

    @Test
    void findAllBySubject() {
        when(studentTaskRepository.findAllBySubject_SubjectName(anyString())).thenReturn(Set.of(exam));

        Set<StudentTask> found = studentTaskSDjpaService.findAllBySubject("Noodles");
        verify(studentTaskRepository, times(1)).findAllBySubject_SubjectName(anyString());
        assertEquals(subjectName, found.stream().filter(studentTask ->
                studentTask.getSubject().getSubjectName().equals("English")).findAny().orElse(null).getSubject().getSubjectName());
    }

    @Test
    void findByDescription() {
        when(studentTaskRepository.findByAssignmentType_Description(anyString())).thenReturn(Optional.of(exam));

        StudentTask found = studentTaskSDjpaService.findByDescription("Homework");
        verify(studentTaskRepository, times(1)).findByAssignmentType_Description(anyString());
        assertEquals(description, found.getAssignmentType().getDescription());
    }

    //default is true
    @Test
    void findByContribution() {
        when(studentTaskRepository.findByContributor(anyBoolean())).thenReturn(Optional.of(exam));

        StudentTask found = studentTaskSDjpaService.findByContribution(true);
        verify(studentTaskRepository, times(1)).findByContributor(anyBoolean());
        assertNotNull(found);
        assertTrue(found.isContributor());
    }

    @Test
    void findByContributionFalse() {
        exam.setContributor(false);     //reported assignment not part of any overall score
        when(studentTaskRepository.findByContributor(anyBoolean())).thenReturn(Optional.of(exam));

        StudentTask found = studentTaskSDjpaService.findByContribution(true);
        verify(studentTaskRepository, times(1)).findByContributor(anyBoolean());
        assertNotNull(found);
        assertFalse(found.isContributor());
    }
}