package com.secure_srm.services.peopleServices;

import com.secure_srm.model.people.Address;
import com.secure_srm.services.BaseService;

public interface AddressService extends BaseService<Address, Long> {
    Address findByPostCode(String postcode);
}
