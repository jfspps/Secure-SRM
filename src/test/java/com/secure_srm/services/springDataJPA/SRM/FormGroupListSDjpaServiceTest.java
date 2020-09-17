package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.repositories.peopleRepos.FormGroupListRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

//see StudentSDjpaServiceTest for commentary
@ExtendWith(MockitoExtension.class)
class FormGroupListSDjpaServiceTest {

    @Mock
    FormGroupListRepository formGroupListRepository;

    Student student1 = Student.builder().firstName("Jack").build();
    Student student2 = Student.builder().firstName("Jill").build();
    TeacherUser teacher = TeacherUser.builder().firstName("Tom").lastName("Jones").build();
    final String groupName = "The kangaroos";
    Set<Student> students = new HashSet<>();

    FormGroupList formGroupList;

    @InjectMocks
    FormGroupListSDjpaService formGroupListSDjpaService;

    @BeforeEach
    void setUp() {
        students.add(student1);
        students.add(student2);
        formGroupList = FormGroupList.builder().teacher(teacher).groupName(groupName).studentList(students).build();
    }

    @Test
    void findByGroupName() {
        when(formGroupListRepository.findByGroupName(any())).thenReturn(Optional.of(formGroupList));

        FormGroupList found = formGroupListSDjpaService.findByGroupName("cheese");
        verify(formGroupListRepository, times(1)).findByGroupName(any());
        assertEquals(groupName, found.getGroupName());
    }

    @Test
    void findByTeacherLastName() {
        when(formGroupListRepository.findByTeacher_LastName(any())).thenReturn(Optional.of(formGroupList));

        FormGroupList found = formGroupListSDjpaService.findByTeacherLastName("Smith");
        verify(formGroupListRepository, times(1)).findByTeacher_LastName(any());
        assertEquals(teacher.getLastName(), found.getTeacher().getLastName());
    }

    @Test
    void findByTeacherFirstAndLastName() {
        when(formGroupListRepository.findByTeacher_FirstNameAndTeacher_LastName(any(), any())).thenReturn(Optional.of(formGroupList));

        FormGroupList found = formGroupListSDjpaService.findByTeacherFirstAndLastName("Jane", "Someone");
        verify(formGroupListRepository, times(1)).findByTeacher_FirstNameAndTeacher_LastName(anyString(), anyString());
        assertEquals(teacher.getLastName(), found.getTeacher().getLastName());
        assertEquals(teacher.getFirstName(), found.getTeacher().getFirstName());
    }
}