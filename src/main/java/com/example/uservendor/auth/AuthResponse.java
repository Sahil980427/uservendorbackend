package com.example.uservendor.auth;

public class AuthResponse {
    private String token;
    private String role;

    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    // Getters
    public String getToken() { return token; }
    public String getRole() { return role; }
}