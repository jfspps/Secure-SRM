package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.repositories.academicRepos.SubjectRepository;
import com.secure_srm.services.academicServices.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class SubjectSDjpaService implements SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectSDjpaService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Subject findBySubjectName(String subjectName) {
        return subjectRepository.findBySubjectName(subjectName).orElse(null);
    }

    @Override
    public Set<Subject> findBySubjectNameContainingIgnoreCase(String subjectTitle) {
        return subjectRepository.findAllBySubjectNameContainingIgnoreCase(subjectTitle);
    }

    @Override
    public Subject save(Subject object) {
        return subjectRepository.save(object);
    }

    @Override
    public Subject findById(Long aLong) {
        Optional<Subject> optional = subjectRepository.findById(aLong);
        if (optional.isEmpty()){
            throw new NotFoundException("Subject not found with ID: " + aLong);
        }
        return optional.get();
    }

    @Override
    public Set<Subject> findAll() {
        Set<Subject> subjects = new HashSet<>();
        subjects.addAll(subjectRepository.findAll());
        return subjects;
    }

    @Override
    public void delete(Subject objectT) {
        subjectRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        subjectRepository.deleteById(aLong);
    }
}
