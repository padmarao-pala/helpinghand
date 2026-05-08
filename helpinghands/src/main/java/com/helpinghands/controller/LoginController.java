package com.helpinghands.controller;

import com.helpinghands.model.User;
import com.helpinghands.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // =========================
    // LOGIN PAGE
    // =========================
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // =========================
    // REGISTER PAGE
    // =========================
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // =========================
    // REGISTER USER
    // =========================
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {

        // Check username exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user
        userRepository.save(user);

        model.addAttribute("success", "Registration successful! Please login.");
        return "login";
    }
}