package com.web_boxx.dashboard.app.dtos;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String phone;

    private LocalDate birthday;

    private String passwordHash;

    private String role;
    private Boolean isActive;
    private Boolean cashierapp;
    private Boolean helperapp;

    private String subscriptionStatus;
    private String usagePlan;
    private List<Map<String, String>> purchaseHistory;
}
