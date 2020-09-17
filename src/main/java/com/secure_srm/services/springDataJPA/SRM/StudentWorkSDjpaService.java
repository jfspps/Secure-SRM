package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.StudentWork;
import com.secure_srm.repositories.academicRepos.StudentWorkRepository;
import com.secure_srm.services.academicServices.StudentWorkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class StudentWorkSDjpaService implements StudentWorkService {

    private final StudentWorkRepository studentWorkRepository;

    public StudentWorkSDjpaService(StudentWorkRepository studentWorkRepository) {
        this.studentWorkRepository = studentWorkRepository;
    }

    @Override
    public StudentWork findByTitle(String title) {
        return studentWorkRepository.findByTitle(title).orElse(null);
    }

    @Override
    public StudentWork findByTeacherLastName(String lastName) {
        return studentWorkRepository.findByTeacherUploader_LastName(lastName).orElse(null);
    }

    @Override
    public StudentWork findByTeacherFirstAndLastName(String firstName, String lastName) {
        return studentWorkRepository.findByTeacherUploader_FirstNameAndTeacherUploader_LastName(firstName, lastName).orElse(null);
    }

    @Override
    public StudentWork findBySubject(String subjectName) {
        return studentWorkRepository.findBySubject_SubjectName(subjectName).orElse(null);
    }

    @Override
    public StudentWork findByDescription(String description) {
        return studentWorkRepository.findByAssignmentType_Description(description).orElse(null);
    }

    @Override
    public StudentWork findByContribution(boolean isAContributor) {
        return studentWorkRepository.findByContributor(isAContributor).orElse(null);
    }

    @Override
    public StudentWork save(StudentWork object) {
        return studentWorkRepository.save(object);
    }

    @Override
    public StudentWork findById(Long aLong) {
        return studentWorkRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<StudentWork> findAll() {
        Set<StudentWork> studentWorks = new HashSet<>();
        studentWorks.addAll(studentWorkRepository.findAll());
        return studentWorks;
    }

    @Override
    public void delete(StudentWork objectT) {
        studentWorkRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        studentWorkRepository.deleteById(aLong);
    }
}
