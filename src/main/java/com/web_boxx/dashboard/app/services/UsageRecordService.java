package com.web_boxx.dashboard.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web_boxx.dashboard.app.models.UsageRecord;
import com.web_boxx.dashboard.app.repositories.UsageRecordRepository;


@Service
public class UsageRecordService {

    @Autowired
    private UsageRecordRepository usageRecordRepository;

    public List<UsageRecord> getAllUsageRecords() {
        return usageRecordRepository.findAll();
    }

    public Optional<UsageRecord> getUsageRecordById(String id) {
        if (!usageRecordRepository.existsById(id)) {
            throw new IllegalArgumentException("UsageRecord with id " + id + " does not exist.");
        }
        return usageRecordRepository.findById(id);
    }

    public UsageRecord createUsageRecord(UsageRecord usageRecord) {
        return usageRecordRepository.save(usageRecord);
    }

    public UsageRecord updateUsageRecord(String id, UsageRecord usageRecord) {
        if (!usageRecordRepository.existsById(id)) {
            throw new IllegalArgumentException("UsageRecord with id " + id + " does not exist.");
        }
        return usageRecordRepository.save(usageRecord);
    }

    public void deleteUsageRecord(String id) {
        if (!usageRecordRepository.existsById(id)) {
            throw new IllegalArgumentException("UsageRecord with id " + id + " does not exist.");
        }
        usageRecordRepository.deleteById(id);
    }
}
