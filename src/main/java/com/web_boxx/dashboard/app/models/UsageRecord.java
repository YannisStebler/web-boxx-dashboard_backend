package com.web_boxx.dashboard.app.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "usage_records")
public class UsageRecord {
    @Id
    private String id;
    
    private String userId;
    private String app; 
    private String action; 
    private String price;
    private LocalDateTime timestamp;
}
