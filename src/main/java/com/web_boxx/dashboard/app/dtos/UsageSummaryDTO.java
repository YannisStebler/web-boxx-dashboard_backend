package com.web_boxx.dashboard.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsageSummaryDTO {
    private String app;
    private String action;
    private double totalPrice;
}
