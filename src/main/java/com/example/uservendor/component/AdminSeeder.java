package com.example.uservendor.component;
import com.example.uservendor.entity.User;
import com.example.uservendor.enumeration.Role;
import com.example.uservendor.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection
    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // The master admin credentials (you can change these later)
        String masterAdminEmail = "admin@vms.com";
        String masterAdminPassword = "AdminPassword123!";

        // 1. Check if the master admin already exists in the uvms database
        Optional<User> adminExists = userRepository.findByEmail(masterAdminEmail);

        // 2. If it does not exist, create it immediately
        if (adminExists.isEmpty()) {
            User masterAdmin = new User();
            masterAdmin.setEmail(masterAdminEmail);

            // Hash the password before saving
            masterAdmin.setPassword(passwordEncoder.encode(masterAdminPassword));

            // Assign max security clearance
            masterAdmin.setRole(Role.ROLE_ADMIN);

            // An admin is never blocked and always enabled
            masterAdmin.setEnabled(true);
            masterAdmin.setBlocked(false);

            // Save to MySQL
            userRepository.save(masterAdmin);

            System.out.println("✅ SYSTEM BOOTSTRAP: Master Admin account created successfully.");
            System.out.println("➡️ Email: " + masterAdminEmail);
        } else {
            System.out.println("✅ SYSTEM BOOTSTRAP: Master Admin already exists. Skipping creation.");
        }
    }
}