package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.people.SubjectClassList;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.repositories.peopleRepos.SubjectClassListRepository;
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
class SubjectClassListSDjpaServiceTest {

    @Mock
    SubjectClassListRepository subjectClassListRepository;

    Student student1 = Student.builder().firstName("Jack").build();
    Student student2 = Student.builder().firstName("Jill").build();
    TeacherUser teacher = TeacherUser.builder().firstName("Tom").lastName("Jones").build();
    final String groupName = "The kangaroos";
    Set<Student> students = new HashSet<>();

    final String subjectName = "History";
    Subject subject = Subject.builder().subjectName(subjectName).build();

    SubjectClassList subjectClassList;

    @InjectMocks
    SubjectClassListSDjpaService subjectClassListSDjpaService;

    @BeforeEach
    void setUp() {
        students.add(student1);
        students.add(student2);
        subjectClassList = SubjectClassList.builder()
                .studentList(students)
                .teacher(teacher)
                .groupName(groupName)
                .subject(subject).build();
    }

    @Test
    void findBySubject() {
        when(subjectClassListRepository.findBySubject_SubjectName(any())).thenReturn(Optional.of(subjectClassList));

        SubjectClassList found = subjectClassListSDjpaService.findBySubject("football");
        verify(subjectClassListRepository, times(1)).findBySubject_SubjectName(any());
        assertEquals(subjectName, found.getSubject().getSubjectName());
    }

    @Test
    void findByTeacherLastName() {
        when(subjectClassListRepository.findByTeacher_LastName(any())).thenReturn(Optional.of(subjectClassList));

        SubjectClassList found = subjectClassListSDjpaService.findByTeacherLastName("Roger");
        verify(subjectClassListRepository, times(1)).findByTeacher_LastName(any());
        assertEquals(teacher.getLastName(), found.getTeacher().getLastName());
    }

    @Test
    void findByGroupName() {
        when(subjectClassListRepository.findByGroupName(any())).thenReturn(Optional.of(subjectClassList));

        SubjectClassList found = subjectClassListSDjpaService.findByGroupName("Cheese");
        verify(subjectClassListRepository, times(1)).findByGroupName(any());
        assertEquals(groupName, found.getGroupName());
    }

    @Test
    void findByTeacherFirstAndLastName() {
        when(subjectClassListRepository.findByTeacher_FirstNameAndTeacher_LastName(any(), any())).thenReturn(Optional.of(subjectClassList));

        SubjectClassList found = subjectClassListSDjpaService.findByTeacherFirstAndLastName("Roger", "Moore");
        verify(subjectClassListRepository, times(1)).findByTeacher_FirstNameAndTeacher_LastName(any(), any());
        assertEquals(teacher.getLastName(), found.getTeacher().getLastName());
        assertEquals(teacher.getFirstName(), found.getTeacher().getFirstName());
    }
}