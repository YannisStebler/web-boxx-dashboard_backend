package com.web_boxx.dashboard.app.delegates;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.web_boxx.dashboard.app.dtos.UserDTO;
import com.web_boxx.dashboard.app.models.User;
import com.web_boxx.dashboard.app.services.UserService;

@Component
public class UserDelegate {

    @Autowired
    private UserService userService;

    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(String id) {
        return userService.getUserById(id)
                .map(this::toDTO);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = toEntity(userDTO);
        User created = userService.createUser(user);
        return toDTO(created);
    }

    public UserDTO updateUser(String id, UserDTO userDTO) {
        User user = toEntity(userDTO);
        user.setId(id);
        User updated = userService.updateUser(id, user);
        return toDTO(updated);
    }

    public void deleteUser(String id) {
        userService.deleteUser(id);
    }

    // DTO → Entity
    private User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setBirthday(dto.getBirthday());
        user.setPasswordHash(dto.getPasswordHash());
        user.setRole(dto.getRole());
        user.setIsActive(dto.getIsActive());
        user.setCashierapp(dto.getCashierapp());
        user.setHelperapp(dto.getHelperapp());
        user.setSubscriptionStatus(dto.getSubscriptionStatus());
        user.setUsagePlan(dto.getUsagePlan());
        user.setPurchaseHistory(dto.getPurchaseHistory());

        // You can set additional internal fields here if needed
        return user;
    }

    // Entity → DTO
    private UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setBirthday(user.getBirthday());
        dto.setPasswordHash(user.getPasswordHash());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCashierapp(user.getCashierapp());
        dto.setHelperapp(user.getHelperapp());
        dto.setSubscriptionStatus(user.getSubscriptionStatus());
        dto.setUsagePlan(user.getUsagePlan());
        dto.setPurchaseHistory(user.getPurchaseHistory());

        return dto;
    }
}
