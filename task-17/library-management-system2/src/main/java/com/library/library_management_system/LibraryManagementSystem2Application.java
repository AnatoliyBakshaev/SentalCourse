package com.library.library_management_system;

import com.library.library_management_system.entity.Role;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.repository.RoleRepository;
import com.library.library_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@SpringBootApplication
public class LibraryManagementSystem2Application implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementSystem2Application.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Создать роли, если их нет
        Role userRole = roleRepository.findByName("USER").orElse(null);
        if (userRole == null) {
            userRole = new Role("USER");
            roleRepository.save(userRole);
            System.out.println("✅ Создана роль USER");
        }

        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole == null) {
            adminRole = new Role("ADMIN");
            roleRepository.save(adminRole);
            System.out.println("✅ Создана роль ADMIN");
        }

        // 2. Создать администратора, если его нет
        User admin = userRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setFullName("Администратор");
            admin.setEmail("admin@library.ru");
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            admin.setRoles(new HashSet<>());
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            System.out.println("✅ Создан администратор: admin / admin123");
        } else {
            // Проверить, есть ли у существующего admin роль ADMIN
            if (!admin.getRoles().contains(adminRole)) {
                admin.getRoles().add(adminRole);
                userRepository.save(admin);
                System.out.println("✅ Добавлена роль ADMIN существующему пользователю");
            }
        }

        System.out.println("📚 Приложение запущено!");
        System.out.println("👤 Админ: admin / admin123");
    }
}