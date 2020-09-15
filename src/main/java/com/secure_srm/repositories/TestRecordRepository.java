package com.secure_srm.repositories;

import com.secure_srm.model.TestRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TestRecordRepository extends JpaRepository<TestRecord, Long> {
    Optional<TestRecord> findByRecordName(String recordName);

    Set<TestRecord> findAllByUser_Username(String username);
}