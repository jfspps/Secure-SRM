package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.repositories.academicRepos.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

//see StudentSDjpaServiceTest for commentary
@ExtendWith(MockitoExtension.class)
class SubjectSDjpaServiceTest {

    @Mock
    SubjectRepository subjectRepository;

    @InjectMocks
    SubjectSDjpaService subjectSDjpaService;

    Subject testSubject;
    final String subjectName = "Mathematics";
    final Long id = 1L;
    final TeacherUser teacher1 = new TeacherUser();
    final TeacherUser teacher2 = new TeacherUser();
    Set<TeacherUser> teacherSet = new HashSet<>();

    @BeforeEach
    void setUp() {
        teacherSet.add(teacher1);
        teacherSet.add(teacher2);
        testSubject = Subject.builder().subjectName(subjectName).teachers(teacherSet).build();
    }

    @Test
    void findBySubjectName() {
        when(subjectRepository.findBySubjectName(subjectName)).thenReturn(Optional.of(testSubject));

        Subject subjectFound = subjectSDjpaService.findBySubjectName(subjectName);
        assertEquals(subjectName, subjectFound.getSubjectName());
        verify(subjectRepository, times(1)).findBySubjectName(anyString());
    }

    @Test
    void saveWithId() {
        when(subjectRepository.save(any())).thenReturn(testSubject);

        Subject subjectSaved = subjectSDjpaService.save(testSubject);
        verify(subjectRepository, times(1)).save(any());
        //JPA save() not persistent at the moment
        assertEquals(0, subjectSDjpaService.findAll().size());
    }

    @Test
    void saveWithoutId() {
        when(subjectRepository.save(any())).thenReturn(new Subject());

        Subject savedWithoutId = subjectSDjpaService.save(testSubject);
        assertNull(savedWithoutId.getSubjectName());
        assertNotNull(savedWithoutId);
        verify(subjectRepository, times(1)).save(any());
    }

    @Test
    void findById() {
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.of(testSubject));

        Subject subjectFound = subjectSDjpaService.findById(id);
        assertNotNull(subjectFound);
        verify(subjectRepository, times(1)).findById(anyLong());
    }

    @Test
    void findByIdNotFound() {
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class, () -> subjectSDjpaService.findById(1L),
                "Expected exception to throw an error. But it didn't"
        );

        // then
        assertTrue(notFoundException.getMessage().contains("Subject not found with ID: 1"));

        verify(subjectRepository).findById(anyLong());
    }

    @Test
    void delete() {
        // see StudentSDjpaServiceTest
        assertEquals(0, subjectSDjpaService.findAll().size());
        subjectSDjpaService.delete(testSubject);
        assertEquals(0, subjectSDjpaService.findAll().size());
    }

    @Test
    void deleteById() {
        // see StudentSDjpaServiceTest
        assertEquals(0, subjectSDjpaService.findAll().size());
        subjectSDjpaService.deleteById(id);
        assertEquals(0, subjectSDjpaService.findAll().size());
    }
}