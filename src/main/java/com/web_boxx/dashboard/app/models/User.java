package com.web_boxx.dashboard.app.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    // General Information
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String phone;

    // Additional Information
    private String birthday;
    private String age;

    // Authentication Information
    private String passwordHash;
    private Boolean emailVerified;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Role & Permission Information
    private String role;
    private Boolean isActive;
    private Boolean cashierapp;
    private Boolean helperapp;

    // Stripe Information
    private String stripeCustomerId;
    private String subscriptionStatus;

    // Usage Plan & Purchase Tracking
    private String usagePlan;
    private List<Map<String, String>> purchaseHistory;

    // App Referration Information
    private String cashierAppUserId;
    private String helperAppUserId;
}
