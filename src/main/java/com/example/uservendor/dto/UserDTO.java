package com.example.uservendor.dto;

import com.example.uservendor.entity.User;

public class UserDTO {
    private Long id;
    private String email;
    private String role;
    private boolean isEnabled;
    private boolean isBlocked;
    private Double lastLatitude;
    private Double lastLongitude;

    // Constructor that maps our Database Entity to this safe DTO
    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.isEnabled = user.isEnabled();
        this.isBlocked = user.isBlocked();
        this.lastLatitude = user.getLastLatitude();
        this.lastLongitude = user.getLastLongitude();
    }

    // Getters for Spring to convert to JSON
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { this.isEnabled = enabled; }
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { this.isBlocked = blocked; }
    public Double getLastLatitude() { return lastLatitude; }
    public Double getLastLongitude() { return lastLongitude; }
}