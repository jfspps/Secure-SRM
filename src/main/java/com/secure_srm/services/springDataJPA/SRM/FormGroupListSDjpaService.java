package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.repositories.peopleRepos.FormGroupListRepository;
import com.secure_srm.services.peopleServices.FormGroupListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class FormGroupListSDjpaService implements FormGroupListService {

    private final FormGroupListRepository formGroupListRepository;

    public FormGroupListSDjpaService(FormGroupListRepository formGroupListRepository) {
        this.formGroupListRepository = formGroupListRepository;
    }

    @Override
    public FormGroupList findByGroupName(String groupName) {
        return formGroupListRepository.findByGroupName(groupName).orElse(null);
    }

    @Override
    public FormGroupList findByTeacherLastName(String lastName) {
        return formGroupListRepository.findByTeacher_LastName(lastName).orElse(null);
    }

    //note here how the name of the JPA method differs from the FormGroupList method name
    //calls to findByTeacherFirstAndLastName() are redirected to call findByTeacher_FirstNameAndTeacher_LastName()
    @Override
    public FormGroupList findByTeacherFirstAndLastName(String firstName, String lastName) {
        return formGroupListRepository.findByTeacher_FirstNameAndTeacher_LastName(firstName, lastName).orElse(null);
    }

    @Override
    public FormGroupList save(FormGroupList object) {
        return formGroupListRepository.save(object);
    }

    @Override
    public FormGroupList findById(Long aLong) {
        return formGroupListRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<FormGroupList> findAll() {
        Set<FormGroupList> formGroupLists = new HashSet<>();
        formGroupLists.addAll(formGroupListRepository.findAll());
        return formGroupLists;
    }

    @Override
    public void delete(FormGroupList objectT) {
        formGroupListRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        formGroupListRepository.deleteById(aLong);
    }
}
