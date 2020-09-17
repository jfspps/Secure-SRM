package com.secure_srm.repositories.peopleRepos;

import com.secure_srm.model.people.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//the implementation of the following methods is supplied automatically by JPA ***

//declares additional SpringDataJPA CRUD functionality for Address
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByPostcode(String postcode);
}
