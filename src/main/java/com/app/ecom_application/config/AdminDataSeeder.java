package com.app.ecom_application.config;

import com.app.ecom_application.Model.UserRole;
import com.app.ecom_application.Model.User;
import com.app.ecom_application.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public AdminDataSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            System.out.println("Admin already exists.");
            return;
        }

        User admin = new User();

        admin.setId(1L);
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail(adminEmail);
        admin.setPhone("9999999999");

        // Never store plain-text password
        admin.setPassword(
                passwordEncoder.encode(adminPassword)
        );

        admin.setRole(UserRole.ADMIN);

        userRepository.save(admin);

        System.out.println("======================================");
        System.out.println("Admin created successfully");
        System.out.println("Email: " + adminEmail);
        System.out.println("Role : ADMIN");
        System.out.println("======================================");
    }
}