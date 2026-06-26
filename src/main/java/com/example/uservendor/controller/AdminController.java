package com.example.uservendor.controller;
import org.springframework.transaction.annotation.Transactional;
import com.example.uservendor.dto.UserDTO;
import com.example.uservendor.entity.User;
import com.example.uservendor.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;

    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // Updated Constructor
    public AdminController(UserRepository userRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. The Master List: Fetch all users and vendors (and their locations)
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> safeUsers = userRepository.findAll()
                .stream()
                .map(UserDTO::new) // Converts User to UserDTO to hide passwords
                .collect(Collectors.toList());

        return ResponseEntity.ok(safeUsers);
    }

    // 2. The Kill Switch: Block or Unblock any User or Vendor
    @PutMapping("/users/{id}/toggle-block")
    @Transactional
    public ResponseEntity<?> toggleUserBlockStatus(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // Security check: Prevent an Admin from blocking themselves or other Admins
        if (user.getRole().name().equals("ROLE_ADMIN")) {
            return ResponseEntity.badRequest().body("Cannot block an Admin account.");
        }

        // Flip the boolean switch
        user.setBlocked(!user.isBlocked());
        userRepository.saveAndFlush(user);

        String status = user.isBlocked() ? "blocked" : "unblocked";
        return ResponseEntity.ok("Account for " + user.getEmail() + " has been " + status + ".");
    }

    // 3. The Approval Switch: Enable or Disable a Vendor
    @PutMapping("/vendors/{id}/toggle-enable")
    @Transactional
    public ResponseEntity<?> toggleVendorEnableStatus(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // Security check: Only Vendors use the "isEnabled" approval logic
        if (!user.getRole().name().equals("ROLE_VENDOR")) {
            return ResponseEntity.badRequest().body("Only Vendors require Admin approval.");
        }

        // Flip the boolean switch
        user.setEnabled(!user.isEnabled());
        userRepository.saveAndFlush(user);

        String status = user.isEnabled() ? "approved and enabled" : "disabled";
        return ResponseEntity.ok("Vendor " + user.getEmail() + " has been " + status + ".");
    }

    // 4. Maximum Security: Create a new Admin (Only accessible by existing Admins)
    @PostMapping("/create-admin")
    public ResponseEntity<?> createNewAdmin(@RequestBody com.example.uservendor.dto.RegisterRequest request) {

        // Check if email is already taken
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return org.springframework.http.ResponseEntity.badRequest().body("Email is already registered.");
        }

        User newAdmin = new User();
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));

        // Grant Max Security Clearance
        newAdmin.setRole(com.example.uservendor.enumeration.Role.ROLE_ADMIN);

        // Admins are always enabled and never blocked by default
        newAdmin.setEnabled(true);
        newAdmin.setBlocked(false);

        userRepository.save(newAdmin);

        return org.springframework.http.ResponseEntity.ok("New System Administrator created successfully.");
    }
}