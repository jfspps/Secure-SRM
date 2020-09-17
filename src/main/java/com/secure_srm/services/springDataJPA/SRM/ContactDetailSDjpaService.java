package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.repositories.peopleRepos.ContactDetailRepository;
import com.secure_srm.services.peopleServices.ContactDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class ContactDetailSDjpaService implements ContactDetailService {

    private final ContactDetailRepository contactDetailRepository;

    public ContactDetailSDjpaService(ContactDetailRepository contactDetailRepository) {
        this.contactDetailRepository = contactDetailRepository;
    }

    @Override
    public ContactDetail findByEmail(String email) {
        return contactDetailRepository.findByEmail(email).orElse(null);
    }

    @Override
    public ContactDetail findByPhone(String phoneNumber) {
        return contactDetailRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }

    @Override
    public ContactDetail save(ContactDetail object) {
        return contactDetailRepository.save(object);
    }

    @Override
    public ContactDetail findById(Long aLong) {
        return contactDetailRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<ContactDetail> findAll() {
        Set<ContactDetail> contactDetails = new HashSet<>();
        contactDetails.addAll(contactDetailRepository.findAll());
        return contactDetails;
    }

    @Override
    public void delete(ContactDetail objectT) {
        contactDetailRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        contactDetailRepository.deleteById(aLong);
    }
}
