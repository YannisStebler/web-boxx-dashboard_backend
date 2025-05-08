package com.web_boxx.dashboard.app.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web_boxx.dashboard.app.models.User;
import com.web_boxx.dashboard.app.repositories.UserRepository;
import com.web_boxx.dashboard.app.security.JwtHelper;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {

        return userRepository.findById(id);
    }

    public User createUser(User user) {

        user.setAge(String.valueOf(LocalDateTime.now().getYear() - user.getBirthday().getYear()));
        user.setEmailVerified(false);

        user.setRole("user");
        user.setIsActive(true);
        user.setCashierapp(false);
        user.setHelperapp(false);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

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
