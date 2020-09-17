package com.secure_srm.services.peopleServices;

import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.services.securityServices.BaseService;

public interface ContactDetailService extends BaseService<ContactDetail, Long> {
    ContactDetail findByEmail(String email);

    ContactDetail findByPhone(String phoneNumber);
}
