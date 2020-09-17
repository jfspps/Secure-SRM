package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.people.Address;
import com.secure_srm.repositories.peopleRepos.AddressRepository;
import com.secure_srm.services.peopleServices.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class AddressSDjpaService implements AddressService {

    private final AddressRepository addressRepository;

    public AddressSDjpaService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Address findByPostCode(String postcode) {
        return addressRepository.findByPostcode(postcode).orElse(null);
    }

    @Override
    public Address save(Address object) {
        return addressRepository.save(object);
    }

    @Override
    public Address findById(Long aLong) {
        return addressRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<Address> findAll() {
        Set<Address> addresses = new HashSet<>();
        addresses.addAll(addressRepository.findAll());
        return addresses;
    }

    @Override
    public void delete(Address objectT) {
        addressRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        addressRepository.deleteById(aLong);
    }
}
