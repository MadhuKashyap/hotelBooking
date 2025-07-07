package com.example.hotelBooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
                .requestMatchers("/users/login").permitAll()
                .requestMatchers("/users/signup").permitAll()
                .requestMatchers("/health").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .httpBasic(AbstractHttpConfigurer::disable) // Disable basic auth
            .formLogin(AbstractHttpConfigurer::disable); // Disable form login
        
        return http.build();
    }
}
