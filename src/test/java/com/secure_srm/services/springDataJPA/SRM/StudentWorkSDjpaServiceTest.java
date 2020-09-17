package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.model.academic.StudentWork;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.repositories.academicRepos.StudentWorkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//see StudentSDjpaServiceTest for commentary
@ExtendWith(MockitoExtension.class)
class StudentWorkSDjpaServiceTest {

    final String teacherLastName = "Jones";
    final String teacherFirstName = "Tom";

    //needed for StudentWork (contributor is set to true by default)
    TeacherUser setter = TeacherUser.builder().firstName(teacherFirstName).lastName(teacherLastName).build();
    final String subjectName = "English";
    Subject subject = Subject.builder().subjectName(subjectName).build();
    final int maxScore = 54;
    final String description = "End of term exam";
    AssignmentType assignmentType = AssignmentType.builder().description(description).build();
    final String assignmentTitle = "The merits of testing";

    StudentWork exam;

    @Mock
    StudentWorkRepository studentWorkRepository;

    @InjectMocks
    StudentWorkSDjpaService studentWorkSDjpaService;

    @BeforeEach
    void setUp() {
        exam = StudentWork.builder()
                .teacherUploader(setter)
                .subject(subject)
                .maxScore(maxScore)
                .contributor(true)
                .assignmentType(assignmentType)
                .title(assignmentTitle).build();
    }

    @Test
    void findByTitle() {
        when(studentWorkRepository.findByTitle(anyString())).thenReturn(Optional.of(exam));

        StudentWork found = studentWorkSDjpaService.findByTitle("The horrors of youth");
        verify(studentWorkRepository, times(1)).findByTitle(anyString());
        assertEquals(assignmentTitle, found.getTitle());
    }

    @Test
    void findByTeacherLastName() {
        when(studentWorkRepository.findByTeacherUploader_LastName(anyString())).thenReturn(Optional.of(exam));

        StudentWork found = studentWorkSDjpaService.findByTeacherLastName("Smith");
        verify(studentWorkRepository, times(1)).findByTeacherUploader_LastName(anyString());
        assertEquals(teacherLastName, found.getTeacherUploader().getLastName());
    }

    @Test
    void findByTeacherFirstAndLastName() {
        when(studentWorkRepository.findByTeacherUploader_FirstNameAndTeacherUploader_LastName(anyString(), anyString()))
                .thenReturn(Optional.of(exam));

        StudentWork found = studentWorkSDjpaService.findByTeacherFirstAndLastName("John", "Smith");
        verify(studentWorkRepository, times(1))
                .findByTeacherUploader_FirstNameAndTeacherUploader_LastName(anyString(), anyString());
        assertEquals(teacherLastName, found.getTeacherUploader().getLastName());
        assertEquals(teacherFirstName, found.getTeacherUploader().getFirstName());
    }

    @Test
    void findBySubject() {
        when(studentWorkRepository.findBySubject_SubjectName(anyString())).thenReturn(Optional.of(exam));

        StudentWork found = studentWorkSDjpaService.findBySubject("Noodles");
        verify(studentWorkRepository, times(1)).findBySubject_SubjectName(anyString());
        assertEquals(subjectName, found.getSubject().getSubjectName());
    }

    @Test
    void findByDescription() {
        when(studentWorkRepository.findByAssignmentType_Description(anyString())).thenReturn(Optional.of(exam));

        StudentWork found = studentWorkSDjpaService.findByDescription("Homework");
        verify(studentWorkRepository, times(1)).findByAssignmentType_Description(anyString());
        assertEquals(description, found.getAssignmentType().getDescription());
    }

    //default is true
    @Test
    void findByContribution() {
        when(studentWorkRepository.findByContributor(anyBoolean())).thenReturn(Optional.of(exam));

        StudentWork found = studentWorkSDjpaService.findByContribution(true);
        verify(studentWorkRepository, times(1)).findByContributor(anyBoolean());
        assertNotNull(found);
        assertTrue(found.isContributor());
    }

    @Test
    void findByContributionFalse() {
        exam.setContributor(false);     //reported assignment not part of any overall score
        when(studentWorkRepository.findByContributor(anyBoolean())).thenReturn(Optional.of(exam));

        StudentWork found = studentWorkSDjpaService.findByContribution(true);
        verify(studentWorkRepository, times(1)).findByContributor(anyBoolean());
        assertNotNull(found);
        assertFalse(found.isContributor());
    }
}