package com.secure_srm.data.services.securityServices;

import com.secure_srm.data.model.TestRecord;

public interface TestRecordService extends BaseService<TestRecord, Long>{
    TestRecord findAllTestRecordsByUsername(String username);

    TestRecord createTestRecord(String recordName, String username);

    TestRecord updateTestRecord(Long testRecordID, Long userID, String recordName);

    void deleteTestRecordAndUpdateUser(Long testRecordID);
}
