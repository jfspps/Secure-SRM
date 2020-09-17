package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.StudentResult;
import com.secure_srm.model.academic.StudentWork;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.repositories.academicRepos.StudentResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//see StudentSDjpaServiceTest for commentary
@ExtendWith(MockitoExtension.class)
class StudentResultSDjpaServiceTest {

    final String teacherLastName = "Jones";
    final String teacherFirstName = "Tom";
    final String studentFirstName = "John";
    final String studentLastName = "Smith";
    final String subjectName = "English";
    Student student = Student.builder().lastName(studentLastName).firstName(studentFirstName).build();
    TeacherUser marker = TeacherUser.builder().firstName(teacherFirstName).lastName(teacherLastName).build();
    TeacherUser setter = TeacherUser.builder().build();
    Subject subject = Subject.builder().subjectName(subjectName).build();

    final String assignmentTitle = "The merits of testing";
    StudentWork exam = StudentWork.builder().teacherUploader(setter).subject(subject).title(assignmentTitle).build();
    final String score = "MERIT";
    final String comments = "A very pleasing start";

    StudentResult studentResult;

    @Mock
    StudentResultRepository studentResultRepository;

    @InjectMocks
    StudentResultSDjpaService studentResultSDjpaService;

    @BeforeEach
    void setUp() {
        studentResult = StudentResult.builder()
                .student(student)
                .teacher(marker)
                .studentWork(exam)
                .score(score)
                .comments(comments).build();
    }

    @Test
    void findByStudentLastName() {
        when(studentResultRepository.findByStudent_LastName(any())).thenReturn(Optional.of(studentResult));

        StudentResult found = studentResultSDjpaService.findByStudentLastName("Smith");
        verify(studentResultRepository, times(1)).findByStudent_LastName(any());
        assertEquals(studentLastName, found.getStudent().getLastName());
    }

    @Test
    void findByStudentFirstAndLastName() {
        when(studentResultRepository.findByStudent_FirstNameAndStudent_LastName(any(), any())).thenReturn(Optional.of(studentResult));

        StudentResult found = studentResultSDjpaService.findByStudentFirstAndLastName("John", "Smith");
        verify(studentResultRepository, times(1)).findByStudent_FirstNameAndStudent_LastName(any(), any());
        assertEquals(studentLastName, found.getStudent().getLastName());
        assertEquals(studentFirstName, found.getStudent().getFirstName());
    }

    @Test
    void findByTeacherLastName() {
        when(studentResultRepository.findByTeacher_LastName(any())).thenReturn(Optional.of(studentResult));

        StudentResult found = studentResultSDjpaService.findByTeacherLastName("Smith");
        verify(studentResultRepository, times(1)).findByTeacher_LastName(any());
        assertEquals(teacherLastName, found.getTeacher().getLastName());
    }

    @Test
    void findByTeacherFirstAndLastName() {
        when(studentResultRepository.findByTeacher_FirstNameAndTeacher_LastName(any(), any())).thenReturn(Optional.of(studentResult));

        StudentResult found = studentResultSDjpaService.findByTeacherFirstAndLastName("John", "Smith");
        verify(studentResultRepository, times(1)).findByTeacher_FirstNameAndTeacher_LastName(any(), any());
        assertEquals(teacherLastName, found.getTeacher().getLastName());
        assertEquals(teacherFirstName, found.getTeacher().getFirstName());
    }

    @Test
    void findByStudentWorkTitle() {
        when(studentResultRepository.findByStudentWork_Title(any())).thenReturn(Optional.of(studentResult));

        StudentResult found = studentResultSDjpaService.findByStudentWorkTitle("The British People");
        verify(studentResultRepository, times(1)).findByStudentWork_Title(any());
        assertEquals(assignmentTitle, found.getStudentWork().getTitle());
    }

    @Test
    void findByScore() {
        when(studentResultRepository.findByScore(any())).thenReturn(Optional.of(studentResult));

        StudentResult found = studentResultSDjpaService.findByScore("Pass");
        verify(studentResultRepository, times(1)).findByScore(any());
        assertEquals(score, found.getScore());
    }
}