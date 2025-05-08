package com.web_boxx.dashboard.app.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.web_boxx.dashboard.app.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByPhone(String phone);
    User findByUsernameOrEmailOrPhone(String username, String email, String phone);
    Optional<User> findByStripeCustomerId(String stripeCustomerId);
}