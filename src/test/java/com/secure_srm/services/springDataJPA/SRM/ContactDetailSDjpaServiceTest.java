package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.repositories.peopleRepos.ContactDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//see StudentSDjpaServiceTest for commentary
@ExtendWith(MockitoExtension.class)
class ContactDetailSDjpaServiceTest {

    @Mock
    ContactDetailRepository contactDetailRepository;

    final String phoneNumber = "027432847328237";
    final String emailAddress = "one@exmaple.com";

    @InjectMocks
    ContactDetailSDjpaService contactDetailSDjpaService;

    ContactDetail contactDetail;

    @BeforeEach
    void setUp() {
        contactDetail = ContactDetail.builder().phoneNumber(phoneNumber).email(emailAddress).build();
    }

    @Test
    void findByEmail() {
        when(contactDetailRepository.findByEmail(any())).thenReturn(Optional.of(contactDetail));

        ContactDetail found = contactDetailSDjpaService.findByEmail("someEmail");
        verify(contactDetailRepository, times(1)).findByEmail(any());
        assertEquals(emailAddress, found.getEmail());
    }

    @Test
    void findByPhone() {
        when(contactDetailRepository.findByPhoneNumber(any())).thenReturn(Optional.of(contactDetail));

        ContactDetail found = contactDetailSDjpaService.findByPhone("12312423553234234dfdsfdsf");
        verify(contactDetailRepository, times(1)).findByPhoneNumber(any());
        assertEquals(phoneNumber, found.getPhoneNumber());
    }
}