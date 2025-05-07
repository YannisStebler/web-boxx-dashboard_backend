package com.web_boxx.dashboard.app.controllers;

import com.web_boxx.dashboard.app.models.UsageRecord;
import com.web_boxx.dashboard.app.repositories.UsageRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usage-records")
public class UsageRecordController {

    @Autowired
    private UsageRecordRepository usageRecordRepository;

    @GetMapping
    public List<UsageRecord> getAllUsageRecords() {
        return usageRecordRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsageRecord> getUsageRecordById(@PathVariable String id) {
        Optional<UsageRecord> usageRecord = usageRecordRepository.findById(id);
        return usageRecord.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public UsageRecord createUsageRecord(@RequestBody UsageRecord usageRecord) {
        return usageRecordRepository.save(usageRecord);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsageRecord> updateUsageRecord(@PathVariable String id, @RequestBody UsageRecord usageRecordDetails) {
        Optional<UsageRecord> optionalUsageRecord = usageRecordRepository.findById(id);
        if (optionalUsageRecord.isPresent()) {
            UsageRecord usageRecord = optionalUsageRecord.get();
            usageRecord.setUserId(usageRecordDetails.getUserId());
            usageRecord.setApp(usageRecordDetails.getApp());
            usageRecord.setAction(usageRecordDetails.getAction());
            usageRecord.setUnits(usageRecordDetails.getUnits());
            usageRecord.setTimestamp(usageRecordDetails.getTimestamp());
            return ResponseEntity.ok(usageRecordRepository.save(usageRecord));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsageRecord(@PathVariable String id) {
        if (usageRecordRepository.existsById(id)) {
            usageRecordRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
