package com.example.uservendor.controller;

import com.example.uservendor.auth.AuthResponse; // Using your AuthResponse DTO
import com.example.uservendor.config.CustomUserDetails;
import com.example.uservendor.config.JwtUtil;
import com.example.uservendor.dto.LoginRequest;
import com.example.uservendor.entity.User;
import com.example.uservendor.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Verify the email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Fetch User Details securely
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 3. SECURITY RULE: Check if the user is blocked
            if (user.isBlocked()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Your account has been suspended. Contact an administrator.");
            }

            // 4. THE LOCATION TRACKER: Update coordinates on every login
            if (loginRequest.getLatitude() != null && loginRequest.getLongitude() != null) {
                user.setLastLatitude(loginRequest.getLatitude());
                user.setLastLongitude(loginRequest.getLongitude());
                userRepository.save(user);
            }

            // 5. Generate the JWT Token using the custom user details
            String jwt = jwtUtil.generateToken(userDetails);
            String role = user.getRole().name();

            // 6. Return response using your AuthResponse DTO
            return ResponseEntity.ok(new AuthResponse(jwt, role));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login Failed: Invalid credentials or account inactive.");
        }
    }
}










//package com.example.uservendor.controller;
//import com.example.uservendor.auth.AuthResponse;
//import com.example.uservendor.config.CustomUserDetails;
//import com.example.uservendor.config.JwtUtil;
//import com.example.uservendor.dto.LoginRequest;
//import com.example.uservendor.entity.User;
//import com.example.uservendor.repository.UserRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*") // Allows your React app to talk to this API
//public class AuthController {
//
//    private final AuthenticationManager authenticationManager;
//    private final JwtUtil jwtUtil;
//    private final UserRepository userRepository;
//
//    // Constructor Injection
//    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
//        this.authenticationManager = authenticationManager;
//        this.jwtUtil = jwtUtil;
//        this.userRepository = userRepository;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        try {
//            // 1. Verify the email and password (this automatically triggers the isBlocked and isEnabled checks)
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//            );
//
//            // 2. If successful, grab the user details
//            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//            User user = userDetails.getUser();
//
//            // 3. Update the exact GPS location in the database (as mapped in Phase 1)
//            if (request.getLatitude() != null && request.getLongitude() != null) {
//                user.setLastLatitude(request.getLatitude());
//                user.setLastLongitude(request.getLongitude());
//                userRepository.save(user); // Save the new coordinates
//            }
//
//            // 4. Generate the JWT
//            String jwt = jwtUtil.generateToken(userDetails);
//
//            // Extract the role to send to React (so React knows which dashboard to render)
//            String role = user.getRole().name();
//
//            // 5. Return the Token and Role
//            return ResponseEntity.ok(new AuthResponse(jwt, role));
//
//        } catch (Exception e) {
//            // If the password is wrong, or the account is blocked/disabled, this catches the error
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed: " + e.getMessage());
//        }
//    }
//}