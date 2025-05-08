package com.web_boxx.dashboard.app.dtos;

import lombok.Data;

@Data
public class UsageRecordDTO {
    private String id;
    
    private String app; 
    private String action; 
    private String price;
}
