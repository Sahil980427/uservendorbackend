package com.example.uservendor.controller;
import com.example.uservendor.dto.RegisterRequest;
import com.example.uservendor.entity.User;
import com.example.uservendor.entity.VendorProfile;
import com.example.uservendor.enumeration.Role;
import com.example.uservendor.repository.UserRepository;
import com.example.uservendor.repository.VendorProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth/register")
@CrossOrigin(origins = "*")
public class RegistrationController {

    private final UserRepository userRepository;
    private final VendorProfileRepository vendorProfileRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection
    public RegistrationController(UserRepository userRepository,
                                  VendorProfileRepository vendorProfileRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vendorProfileRepository = vendorProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. User Registration (Instant Access)
    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already registered.");
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(Role.ROLE_USER);

        // Users get instant access
        newUser.setEnabled(true);
        newUser.setBlocked(false);

        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully. You can now log in.");
    }

    // 2. Vendor Registration (Requires Admin Approval)
    @PostMapping("/vendor")
    public ResponseEntity<?> registerVendor(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already registered.");
        }

        if (request.getBusinessName() == null || request.getBusinessName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Business name is required for vendors.");
        }

        // Step A: Create the User account
        User newVendor = new User();
        newVendor.setEmail(request.getEmail());
        newVendor.setPassword(passwordEncoder.encode(request.getPassword()));
        newVendor.setRole(Role.ROLE_VENDOR);

        // SECURITY GATE: Vendors are disabled by default until Admin approval
        newVendor.setEnabled(false);
        newVendor.setBlocked(false);

        User savedVendor = userRepository.save(newVendor);

        // Step B: Create the linked Vendor Profile
        VendorProfile profile = new VendorProfile();
        profile.setUser(savedVendor);
        profile.setBusinessName(request.getBusinessName());
        // Description can be updated by the vendor later
        profile.setDescription("New Vendor pending setup.");

        vendorProfileRepository.save(profile);

        return ResponseEntity.ok("Vendor application submitted successfully. Please wait for Admin approval.");
    }
}