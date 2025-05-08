package com.web_boxx.dashboard.app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web_boxx.dashboard.app.dtos.UsageSummaryDTO;
import com.web_boxx.dashboard.app.models.UsageRecord;
import com.web_boxx.dashboard.app.models.User;
import com.web_boxx.dashboard.app.repositories.UsageRecordRepository;
import com.web_boxx.dashboard.app.repositories.UserRepository;
import com.web_boxx.dashboard.app.security.JwtHelper;

@Service
public class SubscriptionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsageRecordRepository usageRecordRepository;

    @Autowired
    private JwtHelper jwtHelper;

    public String createSubscription() {

        String userId = jwtHelper.getUserIdFromToken();
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setSubscriptionStatus("active");
            user.setUsagePlan("PayAsYouGo");
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);
            return "Subscription created successfully!";
        } else {
            return "User not found.";
        }
    }

    public String cancelSubscription() {

        String userId = jwtHelper.getUserIdFromToken();
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setSubscriptionStatus("cancelled");
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);
            return "Subscription cancelled successfully!";
        } else {
            return "User not found.";
        }
    }

    public String getSubscriptionStatus() {

        String userId = jwtHelper.getUserIdFromToken();
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            return "Subscription status: " + optionalUser.get().getSubscriptionStatus();
        } else {
            return "User not found.";
        }
    }

    public List<UsageSummaryDTO> getCurrentUsage() {
        String userId = jwtHelper.getUserIdFromToken();
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return Collections.emptyList();
        }

        List<UsageRecord> records = usageRecordRepository.findByUserId(userId);

        // Gruppieren und Summieren
        Map<String, Double> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getApp() + "|" + record.getAction(),
                        Collectors.summingDouble(record -> {
                            try {
                                return Double.parseDouble(record.getPrice());
                            } catch (NumberFormatException e) {
                                return 0.0; // Alternativ: Loggen
                            }
                        })
                ));

        // Map in Liste von DTOs umwandeln
        List<UsageSummaryDTO> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : grouped.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            result.add(new UsageSummaryDTO(parts[0], parts[1], entry.getValue()));
        }

        return result;
    }
}
