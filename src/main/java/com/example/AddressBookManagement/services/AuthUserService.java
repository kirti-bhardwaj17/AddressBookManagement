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
import java.util.Date;
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

        String token = jwtUtil.generateToken(user.getEmail());

        // ✅ Store Token in Database
        user.setJwtToken(token);
        authUserRepository.save(user);

        return token;
    }

    @Transactional
    public String forgotPassword(String email) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        AuthUser user = userOptional.get();
        String token = jwtUtil.generateToken(email);

        user.setResetToken(token);
        user.setTokenExpiry(new Date(System.currentTimeMillis() + 15 * 60 * 1000));
        authUserRepository.save(user);

        emailService.sendEmail(email, "Reset Password",
                "Click the link to reset password: <a href='http://localhost:8080/auth/reset-password?token=" + token + "'>Reset Password</a>");

        return "Password reset token sent to: " + email;
    }

    @Transactional
    public String resetPassword(String token, ResetPasswordDTO resetPasswordDTO) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(jwtUtil.extractEmail(token));

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        AuthUser user = userOptional.get();

        if (user.getTokenExpiry().before(new Date())) {
            throw new IllegalArgumentException("Token expired! Request a new reset token.");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        user.setResetToken(null);
        user.setTokenExpiry(null);
        authUserRepository.save(user);

        return "Password reset successfully!";
    }
}
