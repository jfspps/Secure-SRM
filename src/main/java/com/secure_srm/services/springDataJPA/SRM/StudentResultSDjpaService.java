package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.StudentResult;
import com.secure_srm.repositories.academicRepos.StudentResultRepository;
import com.secure_srm.services.academicServices.StudentResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class StudentResultSDjpaService implements StudentResultService {

    private final StudentResultRepository studentResultRepository;

    public StudentResultSDjpaService(StudentResultRepository studentResultRepository) {
        this.studentResultRepository = studentResultRepository;
    }

    @Override
    public StudentResult findByStudentLastName(String lastName) {
        return studentResultRepository.findByStudent_LastName(lastName).orElse(null);
    }

    @Override
    public StudentResult findByStudentFirstAndLastName(String firstName, String lastName) {
        return studentResultRepository.findByStudent_FirstNameAndStudent_LastName(firstName, lastName).orElse(null);
    }

    @Override
    public StudentResult findByTeacherLastName(String lastName) {
        return studentResultRepository.findByTeacher_LastName(lastName).orElse(null);
    }

    @Override
    public StudentResult findByTeacherFirstAndLastName(String firstName, String lastName) {
        return studentResultRepository.findByTeacher_FirstNameAndTeacher_LastName(firstName, lastName).orElse(null);
    }

    @Override
    public StudentResult findByStudentWorkTitle(String assignmentTitle) {
        return studentResultRepository.findByStudentWork_Title(assignmentTitle).orElse(null);
    }

    @Override
    public StudentResult findByScore(String score) {
        return studentResultRepository.findByScore(score).orElse(null);
    }

    @Override
    public StudentResult save(StudentResult object) {
        return studentResultRepository.save(object);
    }

    @Override
    public StudentResult findById(Long aLong) {
        return studentResultRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<StudentResult> findAll() {
        Set<StudentResult> studentResults = new HashSet<>();
        studentResults.addAll(studentResultRepository.findAll());
        return studentResults;
    }

    @Override
    public void delete(StudentResult objectT) {
        studentResultRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        studentResultRepository.deleteById(aLong);
    }
}
