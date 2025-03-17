package com.example.AddressBookManagement.services;


import com.example.AddressBookManagement.DTO.AuthUserDTO;
import com.example.AddressBookManagement.DTO.LoginDTO;
import com.example.AddressBookManagement.DTO.ResetPasswordDTO;
import com.example.AddressBookManagement.model.AuthUser;
import com.example.AddressBookManagement.repository.AuthUserRepository;
import com.example.AddressBookManagement.Utils.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthUserService(AuthUserRepository authUserRepository, JwtUtil jwtUtil, EmailService emailService) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Transactional
    public String registerUser(AuthUserDTO userDTO) {
        if (authUserRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        AuthUser user = new AuthUser();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        authUserRepository.save(user);

        // 📧 Send Welcome Email
        String subject = "Welcome to Our Website!";
        String message = "Hello " + user.getFirstName() + ",<br>Thank you for registering!";
        emailService.sendEmail(user.getEmail(), subject, message);

        return "User registered successfully!";
    }

    public String loginUser(LoginDTO loginDTO) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found!");
        }

        AuthUser user = userOptional.get();
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password!");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    @Transactional
    public String forgotPassword(String email, String newPassword) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Sorry! We cannot find the user email: " + email);
        }

        AuthUser user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword)); // Hash new password
        authUserRepository.save(user);

        // 📧 Send Confirmation Email
        String subject = "Password Reset Successful";
        String message = "Your password has been successfully reset.";
        emailService.sendEmail(user.getEmail(), subject, message);

        return "Password has been changed successfully!";
    }


    @Transactional
    public String resetPassword(String email, ResetPasswordDTO resetPasswordDTO) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        AuthUser user = userOptional.get();

        // Validate current password
        if (!passwordEncoder.matches(resetPasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect!");
        }

        // Update new password
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        authUserRepository.save(user);

        return "Password reset successfully!";
    }
}

