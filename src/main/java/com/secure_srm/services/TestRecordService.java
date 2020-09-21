package com.secure_srm.services;

import com.secure_srm.model.TestRecord;
import com.secure_srm.model.security.User;

import java.util.List;
import java.util.Set;

public interface TestRecordService {
        TestRecord save(TestRecord object);

        TestRecord createTestRecord(String recordName, String username);

        TestRecord updateTestRecord(Long testRecordID, Long userID, String recordName);

        TestRecord findByRecordName(String recordName);

        TestRecord findById(Long id);

        Set<TestRecord> findAllTestRecordsByUsername(String username);

        Set<TestRecord> findAll();

        List<TestRecord> findAllByOrderById();

        void delete(TestRecord objectT);

        void deleteById(Long id);

        void deleteTestRecordAndUpdateUser(Long testRecordID, User associatedUser);
}
