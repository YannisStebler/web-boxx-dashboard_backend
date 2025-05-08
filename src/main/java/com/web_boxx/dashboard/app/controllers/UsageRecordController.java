package com.web_boxx.dashboard.app.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web_boxx.dashboard.app.delegates.UsageRecordDelegate;
import com.web_boxx.dashboard.app.dtos.UsageRecordDTO;
import com.web_boxx.dashboard.app.security.JwtHelper;

@RestController
@RequestMapping("/api/usage-records")
public class UsageRecordController {

    @Autowired
    private UsageRecordDelegate usageRecordDelegate;

    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping
    public ResponseEntity<List<UsageRecordDTO>> getAllUsageRecords() {

        if (!jwtHelper.getRoleFromToken().toLowerCase().equals("admin")) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(usageRecordDelegate.getAllUsageRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsageRecordDTO> getUsageRecordById(@PathVariable String id) {
        Optional<UsageRecordDTO> usageRecord = usageRecordDelegate.getUsageRecordById(id);
        return usageRecord.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public UsageRecordDTO createUsageRecord(@RequestBody UsageRecordDTO usageRecord) {
        return usageRecordDelegate.createUsageRecord(usageRecord);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsageRecordDTO> updateUsageRecord(@PathVariable String id, @RequestBody UsageRecordDTO usageRecordDetails) {

        if (!jwtHelper.getRoleFromToken().toLowerCase().equals("admin")) {
            return ResponseEntity.status(403).build();
        }

        Optional<UsageRecordDTO> optionalUsageRecord = usageRecordDelegate.getUsageRecordById(id);
        if (optionalUsageRecord.isPresent()) {
            UsageRecordDTO usageRecord = optionalUsageRecord.get();
            usageRecord.setApp(usageRecordDetails.getApp());
            usageRecord.setAction(usageRecordDetails.getAction());
            usageRecord.setUnits(usageRecordDetails.getUnits());
            return ResponseEntity.ok(usageRecordDelegate.updateUsageRecord(id, usageRecord));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsageRecord(@PathVariable String id) {

        if (!jwtHelper.getRoleFromToken().toLowerCase().equals("admin")) {
            return ResponseEntity.status(403).build();
        }

        if (usageRecordDelegate.getUsageRecordById(id).isPresent()) {
            usageRecordDelegate.deleteUsageRecord(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
