package com.example.hotelBooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for REST API
            .authorizeHttpRequests(authz -> authz
                // Public endpoints that don't require authentication
                .requestMatchers("/users/signup", "/health", "/index", "/hotel-app/index").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
    //Must be included if password is not getting encoded otherwise even if we hve not encoded password,
    // spring will by default use it's bCrypt to encode password and even correct credentials will get 401
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
