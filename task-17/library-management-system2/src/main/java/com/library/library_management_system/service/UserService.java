package com.library.library_management_system.service;

import com.library.library_management_system.dto.UserRegistrationDto;
import com.library.library_management_system.dto.UserProfileUpdateDto;
import com.library.library_management_system.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(UserRegistrationDto dto);
    User registerAdmin(UserRegistrationDto dto);
    User authenticate(String username, String password);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAllUsers();
    User updateProfile(Long userId, UserProfileUpdateDto dto);
    void deleteUser(Long userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    boolean isAdmin(User user);

    boolean isAdmin(Long userId);  // ← новый метод
}