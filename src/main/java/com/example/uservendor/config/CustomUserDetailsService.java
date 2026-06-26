package com.example.uservendor.config;
import com.example.uservendor.entity.User;
import com.example.uservendor.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor injection (no Lombok)
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Look up the user in the uvms database by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Wrap our custom User entity inside CustomUserDetails.
        // Spring Security will automatically read the isEnabled() and isAccountNonLocked()
        // flags we set up in the previous step and block the login if either is false.
        return new CustomUserDetails(user);
    }
}