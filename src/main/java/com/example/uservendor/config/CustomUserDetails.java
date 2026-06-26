package com.example.uservendor.config;
import com.example.uservendor.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converts our Role Enum (e.g., ROLE_ADMIN) into a standard Spring Security Authority
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Set to true for our standard system logic
    }

    @Override
    public boolean isAccountNonLocked() {
        // If the user is blocked by the Admin, the account is locked (returns false)
        return !user.isBlocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Set to true for our standard system logic
    }

    @Override
    public boolean isEnabled() {
        // Directly maps to our Admin approval toggle flag
        return user.isEnabled();
    }

    // Helper method to get the underlying user entity if needed later
    public User getUser() {
        return this.user;
    }
}