package com.web_boxx.dashboard.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web_boxx.dashboard.app.dtos.UsageSummaryDTO;
import com.web_boxx.dashboard.app.services.SubscriptionService;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
    
    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/start")
    public ResponseEntity<String> createSubscription() {
        return ResponseEntity.ok(subscriptionService.createSubscription());
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelSubscription() {
        return ResponseEntity.ok(subscriptionService.cancelSubscription());
    }

    @GetMapping("/status")
    public ResponseEntity<String> getSubscriptionStatus() {
        return ResponseEntity.ok(subscriptionService.getSubscriptionStatus());
    }
    
    @GetMapping("/usage")
    public ResponseEntity<List<UsageSummaryDTO>> getCurrentUsage() {
        return ResponseEntity.ok(subscriptionService.getCurrentUsage());
    }
}
