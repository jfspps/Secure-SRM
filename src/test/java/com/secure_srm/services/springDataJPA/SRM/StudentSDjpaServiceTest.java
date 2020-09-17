package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.people.Student;
import com.secure_srm.repositories.peopleRepos.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

//test Spring Data JPA service with Mockito
@ExtendWith(MockitoExtension.class)
class StudentSDjpaServiceTest {

    //mock object needed for JPA functionality
    @Mock
    StudentRepository studentRepository;

    final String firstName = "James";
    final String lastName = "Apps";
    Long id = 1L;
    Student mockStudent;
    Set<Student> studentList = new HashSet<>();

    //directs Mockito to inject above mocks (effectively a constructor)
    @InjectMocks
    StudentSDjpaService studentSDjpaService;

    @BeforeEach
    void setUp() {
        mockStudent = Student.builder().firstName(firstName).lastName(lastName).build();
    }

    @Test
    void findByLastName() {
        //instruct any* call to findByLastName() to return mockStudent
        when(studentRepository.findByLastName(any())).thenReturn(Optional.of(mockStudent));

        //instantiate a new Student with the same details as mockStudent
        //*demonstrates that last name passed is irrelevant
        Student randomStudent = studentSDjpaService.findByLastName("someOtherDude");

        assertEquals(lastName, randomStudent.getLastName());

        // verify that JPA studentRepository's findByLastName() was called when calling SDjpa's findByLastName() was
        // called (this demonstrates that the mapping provided in SDjpa was successful)
        verify(studentRepository).findByLastName(any());
    }

    @Test
    void findByFirstNameAndLastName() {
        when(studentRepository.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.of(mockStudent));

        Student testStudent = studentSDjpaService.findByFirstAndLastName("The", "Mole");
        assertNotNull(testStudent);
        assertEquals(lastName, testStudent.getLastName());
        assertEquals(firstName, testStudent.getFirstName());
        verify(studentRepository, times(1)).findByFirstNameAndLastName(anyString(), anyString());
    }

    @Test
    void findAllByLastNameLike(){
        studentList.add(Student.builder().lastName("Jones").build());
        when(studentRepository.findAllByLastNameLike(anyString())).thenReturn(studentList);

        Set<Student> testList = studentSDjpaService.findAllByLastNameLike("Jones");
        assertEquals(1, testList.size());
        assertEquals("Jones", testList.stream().filter(student -> student.getLastName().equals("Jones")).findFirst().get().getLastName());
    }

    @Test
    void findAllByLastNameLikePartial(){
        studentList.add(Student.builder().lastName("Jones").build());
        when(studentRepository.findAllByLastNameLike(anyString())).thenReturn(studentList);

        Set<Student> students = studentSDjpaService.findAllByLastNameLike("ones");
        assertEquals(1, students.size());
        assertEquals("Jones", students.stream().filter(student -> student.getLastName().equals("Jones")).findFirst().get().getLastName());
    }

    @Test
    void findAllByLastNameLikeIgnoreCase(){
        studentList.add(Student.builder().lastName("Jones").build());
        when(studentRepository.findAllByLastNameLike(anyString())).thenReturn(studentList);

        Set<Student> students = studentSDjpaService.findAllByLastNameLike("JONES");
        assertEquals(1, students.size());
        assertEquals("Jones", students.stream().filter(student -> student.getLastName().equals("Jones")).findFirst().get().getLastName());
    }

    //equivalent to findAll()
    @Test
    void findAllByLastNameLikeBlank(){
        studentList.add(Student.builder().lastName("Jones").build());
        when(studentRepository.findAllByLastNameLike(anyString())).thenReturn(studentList);

        Set<Student> students = studentSDjpaService.findAllByLastNameLike("");
        assertEquals(1, students.size());
        assertEquals("Jones", students.stream().filter(student -> student.getLastName().equals("Jones")).findFirst().get().getLastName());
    }

    //equivalent to findAll()
    @Test
    void findAllByFirstNameLikeAndLastNameLikeTwoBlank(){
        studentList.add(Student.builder().firstName("Tom").lastName("Jones").build());
        when(studentRepository.findAllByFirstNameLikeAndLastNameLike(anyString(), anyString())).thenReturn(studentList);

        Set<Student> students = studentSDjpaService.findAllByFirstNameLikeAndLastNameLike("", "");
        assertEquals(1, students.size());
        assertEquals("Jones", students.stream().filter(student -> student.getLastName().equals("Jones")).findFirst().get().getLastName());
        assertEquals("Tom", students.stream().filter(student -> student.getFirstName().equals("Tom")).findFirst().get().getFirstName());
    }

    @Test
    void findAllByFirstNameLikeAndLastNameLikeOneBlank(){
        studentList.add(Student.builder().firstName("Tom").lastName("Jones").build());
        when(studentRepository.findAllByFirstNameLikeAndLastNameLike(anyString(), anyString())).thenReturn(studentList);

        Set<Student> students = studentSDjpaService.findAllByFirstNameLikeAndLastNameLike("Tom", "");
        assertEquals(1, students.size());
        assertEquals("Jones", students.stream().filter(student -> student.getLastName().equals("Jones")).findFirst().get().getLastName());
        assertEquals("Tom", students.stream().filter(student -> student.getFirstName().equals("Tom")).findFirst().get().getFirstName());
    }

    @Test
    void findAllByFirstNameLikeAndLastNameLikeTwoPartials(){
        studentList.add(Student.builder().firstName("Tom").lastName("Jones").build());
        when(studentRepository.findAllByFirstNameLikeAndLastNameLike(anyString(), anyString())).thenReturn(studentList);

        Set<Student> students = studentSDjpaService.findAllByFirstNameLikeAndLastNameLike("om", "nes");
        assertEquals(1, students.size());
        assertEquals("Jones", students.stream().filter(student -> student.getLastName().equals("Jones")).findFirst().get().getLastName());
        assertEquals("Tom", students.stream().filter(student -> student.getFirstName().equals("Tom")).findFirst().get().getFirstName());
    }


    @Test
    void save() {
        Student StudentToBeSaved = Student.builder().build();

        when(studentRepository.save(any())).thenReturn(mockStudent);

        //savedStudent will be the same as mockStudent; at this stage, we only verify the return of save()
        Student savedStudent = studentSDjpaService.save(StudentToBeSaved);

        assertNotNull(savedStudent);

        verify(studentRepository).save(any());
    }

    @Test
    void findById() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(mockStudent));

        Student studentFound = studentSDjpaService.findById(4L);

        assertNotNull(studentFound);

        verify(studentRepository).findById(anyLong());
    }

    @Test
    void findByIdNotFound() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class, () -> studentSDjpaService.findById(1L),
                "Expected exception to throw an error. But it didn't"
        );

        // then
        assertTrue(notFoundException.getMessage().contains("Student not found with ID: 1"));

        verify(studentRepository).findById(anyLong());
    }

    @Test
    void findAllWithSave() {
        Student student1 = Student.builder().build();
        Student student2 = Student.builder().build();

        studentSDjpaService.save(student1);
        studentSDjpaService.save(student2);

        Set<Student> returnedSet = studentSDjpaService.findAll();

        assertNotNull(returnedSet);
        assertEquals(0, returnedSet.size());
        //save() doesn't seem to persist at the moment...
    }

    @Test
    void delete() {
        //test somewhat meaningless for now...

        studentSDjpaService.save(mockStudent);
        assertEquals(0, studentSDjpaService.findAll().size());
        studentSDjpaService.delete(mockStudent);
        assertEquals(0, studentSDjpaService.findAll().size());
        verify(studentRepository, times(1)).delete(any());
    }

    @Test
    void deleteById() {
        //test somewhat meaningless for now...

        studentSDjpaService.save(mockStudent);
        assertEquals(0, studentSDjpaService.findAll().size());
        studentSDjpaService.deleteById(id);
        assertEquals(0, studentSDjpaService.findAll().size());
        verify(studentRepository).deleteById(anyLong());
    }

}