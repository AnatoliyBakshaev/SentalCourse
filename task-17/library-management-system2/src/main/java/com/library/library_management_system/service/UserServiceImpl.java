package com.library.library_management_system.service;

import com.library.library_management_system.dto.UserRegistrationDto;
import com.library.library_management_system.dto.UserProfileUpdateDto;
import com.library.library_management_system.entity.Role;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.repository.RoleRepository;
import com.library.library_management_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public User registerUser(UserRegistrationDto dto) {
        logger.info("Попытка регистрации пользователя: {}", dto.getUsername());

        if (userRepository.existsByUsername(dto.getUsername())) {
            logger.warn("Регистрация отклонена. Логин уже занят: {}", dto.getUsername());
            throw new RuntimeException("Логин уже занят");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            logger.warn("Регистрация отклонена. Email уже зарегистрирован: {}", dto.getEmail());
            throw new RuntimeException("Email уже зарегистрирован");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> {
                    logger.error("Роль USER не найдена в базе данных");
                    return new RuntimeException("Роль USER не найдена");
                });
        user.setRoles(new HashSet<>());
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);
        logger.info("Пользователь успешно зарегистрирован: ID={}, username={}", savedUser.getId(), savedUser.getUsername());
        return savedUser;
    }

    @Override
    @Transactional
    public User registerAdmin(UserRegistrationDto dto) {
        logger.info("Попытка регистрации администратора: {}", dto.getUsername());
        User user = registerUser(dto);
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> {
                    logger.error("Роль ADMIN не найдена в базе данных");
                    return new RuntimeException("Роль ADMIN не найдена");
                });
        user.getRoles().add(adminRole);
        User savedUser = userRepository.save(user);
        logger.info("Администратор успешно зарегистрирован: ID={}, username={}", savedUser.getId(), savedUser.getUsername());
        return savedUser;
    }

    @Override
    public User authenticate(String username, String password) {
        logger.info("Попытка аутентификации пользователя: {}", username);

        Optional<User> userOpt = userRepository.findByUsernameWithRoles(username);

        if (userOpt.isEmpty()) {
            logger.warn("Аутентификация отклонена. Пользователь не найден: {}", username);
            return null;
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(password)) {
            logger.warn("Аутентификация отклонена. Неверный пароль для пользователя: {}", username);
            return null;
        }

        logger.info("Пользователь успешно аутентифицирован: ID={}, username={}, roles={}",
                user.getId(), user.getUsername(), user.getRoles());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        logger.debug("Поиск пользователя по ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        logger.debug("Поиск пользователя по username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAllUsers() {
        logger.info("Запрос списка всех пользователей");
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, UserProfileUpdateDto dto) {
        logger.info("Обновление профиля пользователя ID={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден для обновления: ID={}", userId);
                    return new RuntimeException("Пользователь не найден");
                });

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());

        User updatedUser = userRepository.save(user);
        logger.info("Профиль пользователя обновлён: ID={}, username={}", updatedUser.getId(), updatedUser.getUsername());
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        logger.warn("Удаление пользователя: ID={}", userId);
        userRepository.deleteById(userId);
        logger.info("Пользователь удалён: ID={}", userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isAdmin(User user) {
        if (user == null) {
            logger.debug("Проверка isAdmin: user = null");
            return false;
        }
        boolean result = user.hasRole("ADMIN");
        logger.debug("Проверка isAdmin для пользователя {}: {}", user.getUsername(), result);
        return result;
    }

    @Override
    public boolean isAdmin(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.debug("Проверка isAdmin: пользователь не найден ID={}", userId);
            return false;
        }
        return isAdmin(user);
    }
}