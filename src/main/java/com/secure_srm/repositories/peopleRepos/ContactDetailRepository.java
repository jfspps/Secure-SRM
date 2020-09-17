package com.secure_srm.repositories.peopleRepos;

import com.secure_srm.model.people.ContactDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//the implementation of the following methods is supplied automatically by JPA

//declares additional SpringDataJPA CRUD functionality for ContactDetail
public interface ContactDetailRepository extends JpaRepository<ContactDetail, Long> {

    Optional<ContactDetail> findByEmail(String email);

    Optional<ContactDetail> findByPhoneNumber(String phoneNumber);
}
