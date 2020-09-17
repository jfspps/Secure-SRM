package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.ThresholdList;
import com.secure_srm.repositories.academicRepos.ThresholdListRepository;
import com.secure_srm.services.academicServices.ThresholdListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class ThresholdListSDjpaService implements ThresholdListService {

    private final ThresholdListRepository thresholdListRepository;

    public ThresholdListSDjpaService(ThresholdListRepository thresholdListRepository) {
        this.thresholdListRepository = thresholdListRepository;
    }

    @Override
    public ThresholdList save(ThresholdList thresholdList) {
        return thresholdListRepository.save(thresholdList);
    }

    @Override
    public ThresholdList findById(Long aLong) {
        return thresholdListRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<ThresholdList> findAll() {
        Set<ThresholdList> thresholdLists = new HashSet<>();
        thresholdLists.addAll(thresholdListRepository.findAll());
        return thresholdLists;
    }

    @Override
    public void delete(ThresholdList objectT) {
        thresholdListRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        thresholdListRepository.deleteById(aLong);
    }
}
