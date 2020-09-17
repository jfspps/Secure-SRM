package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.repositories.academicRepos.AssignmentTypeRepository;
import com.secure_srm.services.academicServices.AssignmentTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class AssignmentTypeSDjpaService implements AssignmentTypeService {

    private final AssignmentTypeRepository assignmentTypeRepository;

    public AssignmentTypeSDjpaService(AssignmentTypeRepository assignmentTypeRepository) {
        this.assignmentTypeRepository = assignmentTypeRepository;
    }

    @Override
    public AssignmentType findByDescription(String description) {
        return assignmentTypeRepository.findByDescription(description).orElse(null);
    }

    @Override
    public AssignmentType save(AssignmentType object) {
        return assignmentTypeRepository.save(object);
    }

    @Override
    public AssignmentType findById(Long aLong) {
        return assignmentTypeRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<AssignmentType> findAll() {
        Set<AssignmentType> assignmentTypes = new HashSet<>();
        assignmentTypes.addAll(assignmentTypeRepository.findAll());
        return assignmentTypes;
    }

    @Override
    public void delete(AssignmentType objectT) {
        assignmentTypeRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        assignmentTypeRepository.deleteById(aLong);
    }
}
