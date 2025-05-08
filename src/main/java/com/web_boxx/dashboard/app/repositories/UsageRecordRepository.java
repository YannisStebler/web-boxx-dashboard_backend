package com.web_boxx.dashboard.app.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.web_boxx.dashboard.app.models.UsageRecord;

public interface UsageRecordRepository extends MongoRepository<UsageRecord, String> {
    List<UsageRecord> findByUserId (String userId);
    List<UsageRecord> findByUserIdAndTimestamp (String userId, String timestamp);

    List<UsageRecord> findByUserIdAndApp (String userId, String app);
    List<UsageRecord> findByUserIdAndAppAndTimestamp (String userId, String app, String timestamp);
}
