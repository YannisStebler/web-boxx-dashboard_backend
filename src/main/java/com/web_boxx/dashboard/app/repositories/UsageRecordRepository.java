package com.web_boxx.dashboard.app.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.web_boxx.dashboard.app.models.UsageRecord;

public interface UsageRecordRepository extends MongoRepository<UsageRecord, String> {
    UsageRecord findByUserId (String userId);
    UsageRecord findByUserIdAndDate (String userId, String timestamp);

    UsageRecord findByUserIdAndApp (String userId, String app);
    UsageRecord findByUserIdAndAppAndDate (String userId, String app, String timestamp);
}
