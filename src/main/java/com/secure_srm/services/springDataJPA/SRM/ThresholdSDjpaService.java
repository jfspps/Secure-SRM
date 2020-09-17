package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.Threshold;
import com.secure_srm.repositories.academicRepos.ThresholdRepository;
import com.secure_srm.services.academicServices.ThresholdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class ThresholdSDjpaService implements ThresholdService {

    private final ThresholdRepository thresholdRepository;

    public ThresholdSDjpaService(ThresholdRepository thresholdRepository) {
        this.thresholdRepository = thresholdRepository;
    }

    @Override
    public Threshold findByNumericalBoundary(int numericalBoundary) {
        return thresholdRepository.findByNumerical(numericalBoundary).orElse(null);
    }

    @Override
    public Threshold findByLetterGrade(String letterGrade) {
        return thresholdRepository.findByAlphabetical(letterGrade).orElse(null);
    }

    @Override
    public Threshold save(Threshold object) {
        return thresholdRepository.save(object);
    }

    @Override
    public Threshold findById(Long aLong) {
        return thresholdRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<Threshold> findAll() {
        Set<Threshold> thresholds = new HashSet<>();
        thresholds.addAll(thresholdRepository.findAll());
        return thresholds;
    }

    @Override
    public void delete(Threshold objectT) {
        thresholdRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        thresholdRepository.deleteById(aLong);
    }
}
