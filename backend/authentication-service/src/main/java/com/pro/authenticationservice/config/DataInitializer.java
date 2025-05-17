package com.pro.authenticationservice.config;

import com.pro.authenticationservice.model.Role;
import com.pro.authenticationservice.model.User;
import com.pro.authenticationservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if admin user already exists
            if (!userRepository.existsByUsername("admin")) {
                // Create admin user
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin"));
                adminUser.setEmail("admin@example.com");
                adminUser.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
                userRepository.save(adminUser);
                System.out.println("Admin user created successfully");
            }

            // Check if test user already exists
            if (!userRepository.existsByUsername("user")) {
                // Create regular user
                User regularUser = new User();
                regularUser.setUsername("user");
                regularUser.setPassword(passwordEncoder.encode("password"));
                regularUser.setEmail("user@example.com");
                regularUser.setRoles(Set.of(Role.ROLE_USER));
                userRepository.save(regularUser);
                System.out.println("Regular user created successfully");
            }
        };
    }
}