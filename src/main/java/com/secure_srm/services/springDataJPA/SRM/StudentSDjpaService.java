package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.people.Student;
import com.secure_srm.repositories.peopleRepos.StudentRepository;
import com.secure_srm.services.peopleServices.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// both StudentSDjpaService and StudentServiceMap are implementations of StudentService, so set a profile to
// pick one which operates as runtime (eventually, this dependency is decided later)

// SpringData JPA is only injected if application.yml is set to springDataJPA; the default is StudentServiceMap

// The interface StudentService lists the minimum CRUD functionality and custom findBy...() methods
// The interface StudentRepository declares and defines the CRUD JPA methods.
// This class maps the JPA methods from the repository with the service methods listed in StudentService
// (which may be also be implemented by other services)
@Slf4j
@Service
@Profile("SDjpa")
public class StudentSDjpaService implements StudentService {

    //JPA methods defined under StudentRepository, hence made accessible via an object studentRepository
    private final StudentRepository studentRepository;

    public StudentSDjpaService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student findByLastName(String lastName) {
        return studentRepository.findByLastName(lastName).orElse(null);
    }

    @Override
    public Student findByFirstAndLastName(String firstName, String lastName) {
        return studentRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);
    }

    @Override
    public Student findByFirstLastAndMiddleNames(String firstName, String lastName, String middleNames) {
        return studentRepository.findByFirstNameAndLastNameAndMiddleNames(firstName, lastName, middleNames).orElse(null);
    }

    @Override
    public Set<Student> findAllByLastNameLike(String lastName) {
        return new HashSet<>(studentRepository.findAllByLastNameLike(lastName));
    }

    @Override
    public Set<Student> findAllByLastNameContainingIgnoreCase(String lastName) {
        return new HashSet<>(studentRepository.findAllByLastNameContainingIgnoreCase(lastName));
    }

    @Override
    public Set<Student> findAllByFirstNameLikeAndLastNameLike(String firstName, String lastName) {
        return new HashSet<>(studentRepository.findAllByFirstNameLikeAndLastNameLike(firstName, lastName));
    }

    @Override
    public List<Student> findAllByOrderByLastName() {
        return studentRepository.findAllByOrderByLastName();
    }

    @Override
    public Student save(Student object) {
        return studentRepository.save(object);
    }

    @Override
    public Student findById(Long aLong) {
        //findById returns an Optional<> which is never null (aLong can be null)
        //if none found with given aLong then return null
        Optional<Student> optional = studentRepository.findById(aLong);
        if (optional.isEmpty()){
            throw new NotFoundException("Student not found with ID: " + aLong);
        }
        return optional.get();
    }

    //passing a JPA iterable<T> to a HashSet<T>
    @Override
    public Set<Student> findAll() {
        //pass each student from the repo to students and return
        return new HashSet<>(studentRepository.findAll());
    }

    @Override
    public void delete(Student objectT) {
        studentRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        studentRepository.deleteById(aLong);
    }
}
