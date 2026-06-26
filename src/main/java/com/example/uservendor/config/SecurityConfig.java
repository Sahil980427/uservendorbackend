package com.example.uservendor.config;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Disabled for REST APIs using JWTs
                .authorizeHttpRequests(auth -> auth
                        // 1. Public Endpoints (Anyone can access)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. Admin Endpoints (STRICTLY Admin only)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 3. Vendor Endpoints (Vendors only)
                        .requestMatchers("/api/vendor/**").hasRole("VENDOR")

                        // 4. User Endpoints (Users only)
                        .requestMatchers("/api/user/**").hasRole("USER")

                        // 5. Everything else requires authentication
                        .anyRequest().authenticated()
                )
                // We use stateless sessions because React will send a JWT on every request
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        // Note: We will inject our JWT filter here in the next step
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Explicitly allow your Vite React frontend
        configuration.setAllowedOrigins(List.of("http://localhost:5173",
                "https://uservendorfrontend.vercel.app", // Replace with your actual Vercel URL!
                "*" // Or just use the wildcard to allow everything while you test
        ));
        // Allow all standard HTTP methods, including the OPTIONS preflight
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow the JWT Authorization header to pass through
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this rule to all API endpoints
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // This tells Spring how to hash passwords before saving them to the uvms database
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}