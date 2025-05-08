package com.web_boxx.dashboard.app.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.web_boxx.dashboard.app.models.User;
import com.web_boxx.dashboard.app.repositories.UserRepository;

@Service
public class UserService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {

        return userRepository.findById(id);
    }

    public User createUser(User user) {

        user.setAge(String.valueOf(LocalDateTime.now().getYear() - LocalDate.parse(user.getBirthday()).getYear()));
        user.setEmailVerified(false);

        user.setRole("user");
        user.setIsActive(true);
        user.setCashierapp(false);
        user.setHelperapp(false);

        user.setStripeCustomerId(null);
        user.setSubscriptionStatus("inactive");

        user.setUsagePlan("free");
        user.setPurchaseHistory(null);
        user.setCashierAppUserId(null);
        user.setHelperAppUserId(null);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Stripe.apiKey = stripeApiKey;

        try {
            // Stripe Customer erstellen
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .setName(user.getFirstname() + " " + user.getLastname())
                    .build();

            Customer stripeCustomer = Customer.create(customerParams);

            // Stripe-Customer-ID setzen
            user.setStripeCustomerId(stripeCustomer.getId());

        } catch (StripeException e) {
            throw new RuntimeException("Fehler beim Erstellen des Stripe-Kontos: " + e.getMessage(), e);
        }

        return userRepository.save(user);
    }

    public User updateUser(String id, User userDetails) {
        userDetails.setId(id);

        userDetails.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(userDetails);
    }

    public void deleteUser(String id) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(u -> {
            u.setIsActive(false);
        });
        userRepository.save(user.get());
    }
}
