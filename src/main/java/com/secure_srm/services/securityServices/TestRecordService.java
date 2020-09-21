package com.secure_srm.services.securityServices;

import com.secure_srm.model.TestRecord;
import com.secure_srm.services.BaseService;

import java.util.List;
import java.util.Set;

public interface TestRecordService extends BaseService<TestRecord, Long> {
    Set<TestRecord> findAllTestRecordsByUsername(String username);

    TestRecord createTestRecord(String recordName, String username);

    TestRecord updateTestRecord(Long testRecordID, Long userID, String recordName);

    void deleteTestRecordAndUpdateUser(Long testRecordID);

    List<TestRecord> findAllByOrderById();
}
