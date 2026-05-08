package com.helpinghands.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

            	    // ✅ PUBLIC (VERY IMPORTANT)
            	    .requestMatchers(
            	            "/", 
            	            "/children",
            	            "/donate/**",
            	            "/create-order/**",   // 🔥 change this
            	            "/payment/**",        // 🔥 add this (future use)
            	            "/uploads/**",
            	            "/css/**",
            	            "/js/**"
            	    ).permitAll()

            	    // ✅ ADMIN ONLY
            	    .requestMatchers(
            	            "/admin/**",
            	            "/add-child",
            	            "/save-child",
            	            "/edit-child/**",
            	            "/update-child",
            	            "/admin/contacts",
            	            "/delete-child/**"
            	    ).hasRole("ADMIN")

            	    // ✅ ALLOW EVERYTHING ELSE (IMPORTANT CHANGE)
            	    .anyRequest().permitAll()   // 🔥 CHANGE THIS
            	)

            // ✅ LOGIN
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin", true)
                .permitAll()
            )

            // ✅ LOGOUT
            .logout(logout -> logout
                .logoutSuccessUrl("/")   // ✅ go to home page
                .permitAll()
            );

        return http.build();
    }

    // ✅ ADMIN USER
    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {

        var user = org.springframework.security.core.userdetails.User
                .withDefaultPasswordEncoder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build();

        return new org.springframework.security.provisioning.InMemoryUserDetailsManager(user);
    }
}