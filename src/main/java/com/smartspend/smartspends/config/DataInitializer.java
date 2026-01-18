package com.smartspend.smartspends.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if super admin already exists
        if (!userRepository.existsByMobileNumber("9999999999")) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setMobileNumber("9999999999");
            superAdmin.setPassword(passwordEncoder.encode("superadmin123"));
            superAdmin.setEmail("admin@smartspends.com");
            superAdmin.setRole("sUPEERADMIN");

            userRepository.save(superAdmin);
            System.out.println("Super Admin user created successfully!");
        } else {
            System.out.println("Super Admin user already exists!");
        }
    }
}
