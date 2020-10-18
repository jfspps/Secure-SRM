package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.repositories.academicRepos.StudentTaskRepository;
import com.secure_srm.services.academicServices.StudentTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class StudentTaskSDjpaService implements StudentTaskService {

    private final StudentTaskRepository studentTaskRepository;

    public StudentTaskSDjpaService(StudentTaskRepository studentTaskRepository) {
        this.studentTaskRepository = studentTaskRepository;
    }

    @Override
    public StudentTask findByTitle(String title) {
        return studentTaskRepository.findByTitle(title).orElse(null);
    }

    @Override
    public StudentTask findByTitleAndTeacherUploaderId(String title, Long id) {
        return studentTaskRepository.findByTitleAndTeacherUploader_Id(title, id).orElse(null);
    }

    @Override
    public StudentTask findByTeacherLastName(String lastName) {
        return studentTaskRepository.findByTeacherUploader_LastName(lastName).orElse(null);
    }

    @Override
    public StudentTask findByTeacherFirstAndLastName(String firstName, String lastName) {
        return studentTaskRepository.findByTeacherUploader_FirstNameAndTeacherUploader_LastName(firstName, lastName).orElse(null);
    }

    @Override
    public Set<StudentTask> findAllBySubject(String subjectName) {
        return studentTaskRepository.findAllBySubject_SubjectName(subjectName);
    }

    @Override
    public StudentTask findByDescription(String description) {
        return studentTaskRepository.findByAssignmentType_Description(description).orElse(null);
    }

    @Override
    public StudentTask findByContribution(boolean isAContributor) {
        return studentTaskRepository.findByContributor(isAContributor).orElse(null);
    }

    @Override
    public StudentTask save(StudentTask object) {
        return studentTaskRepository.save(object);
    }

    @Override
    public StudentTask findById(Long aLong) {
        return studentTaskRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<StudentTask> findAll() {
        Set<StudentTask> studentTasks = new HashSet<>();
        studentTasks.addAll(studentTaskRepository.findAll());
        return studentTasks;
    }

    @Override
    public void delete(StudentTask objectT) {
        studentTaskRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        studentTaskRepository.deleteById(aLong);
    }
}
