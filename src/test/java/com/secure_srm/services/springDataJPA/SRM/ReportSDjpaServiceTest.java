package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.Report;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.repositories.academicRepos.ReportRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//see StudentSDjpaServiceTest for commentary
@ExtendWith(MockitoExtension.class)
class ReportSDjpaServiceTest {

    final String teacherLastName = "Jones";
    final String teacherFirstName = "Tom";
    final String studentFirstName = "John";
    final String studentLastName = "Smith";
    final String subjectName = "English";
    Student student = Student.builder().lastName(studentLastName).firstName(studentFirstName).build();
    TeacherUser marker = TeacherUser.builder().firstName(teacherFirstName).lastName(teacherLastName).build();
    Subject subject = Subject.builder().subjectName(subjectName).build();
    final String comments = "Well done";

    Report report;

    @Mock
    ReportRepository reportRepository;

    @InjectMocks
    ReportSDjpaService reportSDjpaService;

    @BeforeEach
    void setUp() {
        report = Report.builder().student(student).teacher(marker).subject(subject).comments(comments).build();
    }

    @Test
    void findByStudentLastName() {
        when(reportRepository.findByStudent_LastName(any())).thenReturn(Optional.of(report));

        Report found = reportSDjpaService.findByStudentLastName("Smith");
        verify(reportRepository, times(1)).findByStudent_LastName(any());
        assertEquals(studentLastName, found.getStudent().getLastName());
    }

    @Test
    void findByStudentFirstAndLastName() {
        when(reportRepository.findByStudent_FirstNameAndStudent_LastName(any(), any())).thenReturn(Optional.of(report));

        Report found = reportSDjpaService.findByStudentFirstAndLastName("John", "Smith");
        verify(reportRepository, times(1)).findByStudent_FirstNameAndStudent_LastName(any(), any());
        assertEquals(studentLastName, found.getStudent().getLastName());
        assertEquals(studentFirstName, found.getStudent().getFirstName());
    }

    @Test
    void findByTeacherLastName() {
        when(reportRepository.findByTeacher_LastName(any())).thenReturn(Optional.of(report));

        Report found = reportSDjpaService.findByTeacherLastName("Smith");
        verify(reportRepository, times(1)).findByTeacher_LastName(any());
        assertEquals(teacherLastName, found.getTeacher().getLastName());
    }

    @Test
    void findByTeacherFirstAndLastName() {
        when(reportRepository.findByTeacher_FirstNameAndTeacher_LastName(any(), any())).thenReturn(Optional.of(report));

        Report found = reportSDjpaService.findByTeacherFirstAndLastName("John", "Smith");
        verify(reportRepository, times(1)).findByTeacher_FirstNameAndTeacher_LastName(any(), any());
        assertEquals(teacherLastName, found.getTeacher().getLastName());
        assertEquals(teacherFirstName, found.getTeacher().getFirstName());
    }

    @Test
    void findBySubject() {
        when(reportRepository.findBySubject_SubjectName(any())).thenReturn(Optional.of(report));

        Report found = reportSDjpaService.findBySubject("Cricket");
        verify(reportRepository, times(1)).findBySubject_SubjectName(any());
        assertEquals(subjectName, found.getSubject().getSubjectName());
    }

    @Test
    void findAllByStudentNames() {
        when(reportRepository.findAllByStudent_FirstNameAndStudent_MiddleNamesAndStudent_LastName(
                anyString(), anyString(), anyString())).thenReturn(new HashSet<>(Set.of(report)));

        Set<Report> reportsFound = reportSDjpaService.findAllByStudentFirstMiddleAndLastNames(
                studentFirstName, "", studentLastName);

        verify(reportRepository, times(1))
                .findAllByStudent_FirstNameAndStudent_MiddleNamesAndStudent_LastName(
                        anyString(), anyString(), anyString());

        assertEquals(subjectName, reportsFound.stream().findAny().get().getSubject().getSubjectName());
    }
}