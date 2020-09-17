package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.Threshold;
import com.secure_srm.model.academic.ThresholdList;
import com.secure_srm.repositories.academicRepos.ThresholdListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//see StudentSDjpaServiceTest for commentary
@ExtendWith(MockitoExtension.class)
class ThresholdListSDjpaServiceTest {

    Threshold threshold1 = Threshold.builder().numerical(80).alphabetical("B").build();
    Threshold threshold2 = Threshold.builder().numerical(85).alphabetical("A").build();
    Set<Threshold> thresholds = new HashSet<>();
    ThresholdList thresholdList;
    final Long id = 1L;

    @Mock
    ThresholdListRepository thresholdListRepository;

    @InjectMocks
    ThresholdListSDjpaService thresholdListSDjpaService;

    @BeforeEach
    void setUp() {
        thresholds.add(threshold1);
        thresholds.add(threshold2);
        thresholdList = ThresholdList.builder().thresholds(thresholds).build();
    }

    @Test
    void save() {
        when(thresholdListRepository.save(any())).thenReturn(thresholdList);

        ThresholdList saved = thresholdListSDjpaService.save(new ThresholdList());
        verify(thresholdListRepository, times(1)).save(any());
        assertNotNull(saved);
        assertEquals(0, thresholdListSDjpaService.findAll().size());    //still not implemented yet
    }

    @Test
    void findById() {
        when(thresholdListRepository.findById(anyLong())).thenReturn(Optional.of(thresholdList));

        ThresholdList saved = thresholdListSDjpaService.findById(100L);
        verify(thresholdListRepository, times(1)).findById(anyLong());
        assertNotNull(saved);
    }

    @Test
    void delete() {
        //revisit this later
        thresholdListSDjpaService.save(thresholdList);
        assertEquals(0, thresholdListSDjpaService.findAll().size());
        thresholdListSDjpaService.delete(thresholdList);
        assertEquals(0, thresholdListSDjpaService.findAll().size());
        verify(thresholdListRepository, times(1)).delete(any());
    }

    @Test
    void deleteById() {
        //revisit this later
        thresholdListSDjpaService.save(thresholdList);
        assertEquals(0, thresholdListSDjpaService.findAll().size());
        thresholdListSDjpaService.deleteById(id);
        assertEquals(0, thresholdListSDjpaService.findAll().size());
        verify(thresholdListRepository, times(1)).deleteById(any());
    }
}