package com.web_boxx.dashboard.app.delegates;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.web_boxx.dashboard.app.dtos.UsageRecordDTO;
import com.web_boxx.dashboard.app.models.UsageRecord;
import com.web_boxx.dashboard.app.security.JwtHelper;
import com.web_boxx.dashboard.app.services.UsageRecordService;

@Component
public class UsageRecordDelegate {
    
    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UsageRecordService usageRecordService;

    public List<UsageRecordDTO> getAllUsageRecords() {
        return usageRecordService.getAllUsageRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsageRecordDTO> getUsageRecordById(String id) {
        return usageRecordService.getUsageRecordById(id)
                .map(this::toDTO);
    }

    public UsageRecordDTO createUsageRecord(UsageRecordDTO usageRecordDTO) {
        UsageRecord usageRecord = toEntity(usageRecordDTO);

        usageRecord.setUserId(jwtHelper.getUserIdFromToken());
        usageRecord.setTimestamp(LocalDateTime.now());

        UsageRecord created = usageRecordService.createUsageRecord(usageRecord);
        return toDTO(created);
    }

    public UsageRecordDTO updateUsageRecord(String id, UsageRecordDTO usageRecordDTO) {
        UsageRecord usageRecord = toEntity(usageRecordDTO);
        usageRecord.setId(id);
        usageRecord.setTimestamp(LocalDateTime.now());
        UsageRecord updated = usageRecordService.updateUsageRecord(id, usageRecord);
        return toDTO(updated);
    }

    public void deleteUsageRecord(String id) {
        usageRecordService.deleteUsageRecord(id);
    }

    // DTO → Entity
    private UsageRecord toEntity(UsageRecordDTO dto) {
        if (dto == null) return null;

        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setId(dto.getId());
        usageRecord.setApp(dto.getApp());
        usageRecord.setAction(dto.getAction());
        usageRecord.setPrice(dto.getPrice());

        return usageRecord;
    }

    // Entity → DTO
    private UsageRecordDTO toDTO(UsageRecord usageRecord) {
        if (usageRecord == null) return null;

        UsageRecordDTO dto = new UsageRecordDTO();
        dto.setId(usageRecord.getId());
        dto.setApp(usageRecord.getApp());
        dto.setAction(usageRecord.getAction());
        dto.setPrice(usageRecord.getPrice());

        return dto;
    }
}
