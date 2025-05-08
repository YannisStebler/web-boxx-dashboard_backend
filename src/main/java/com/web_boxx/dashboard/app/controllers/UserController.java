package com.web_boxx.dashboard.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web_boxx.dashboard.app.delegates.UserDelegate;
import com.web_boxx.dashboard.app.dtos.UserDTO;
import com.web_boxx.dashboard.app.security.JwtHelper;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserDelegate userDelegate;

    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        System.out.println("User ID from token: " + jwtHelper.getUserIdFromToken());
        System.out.println("User role from token: " + jwtHelper.getRoleFromToken());
        if (!jwtHelper.getRoleFromToken().toLowerCase().equals("admin")) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(userDelegate.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {

        if (!jwtHelper.getUserIdFromToken().equals(id) && !jwtHelper.getRoleFromToken().equals("admin")) {
            return ResponseEntity.status(403).build();
        }

        return userDelegate.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO user) {
        return userDelegate.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody UserDTO user) {

        if (!jwtHelper.getUserIdFromToken().equals(id) && !jwtHelper.getRoleFromToken().equals("admin")) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(userDelegate.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {

        if (!jwtHelper.getUserIdFromToken().equals(id) && !jwtHelper.getRoleFromToken().equals("admin")) {
            return ResponseEntity.status(403).build();
        }

        userDelegate.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
