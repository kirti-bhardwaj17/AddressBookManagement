package com.example.AddressBookManagement.controller;

import com.example.AddressBookManagement.DTO.AuthUserDTO;
import com.example.AddressBookManagement.DTO.LoginDTO;
import com.example.AddressBookManagement.DTO.ResetPasswordDTO;
import com.example.AddressBookManagement.services.AuthUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthUserController {

    private final AuthUserService authUserService;

    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user with email and password.")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody AuthUserDTO userDTO) {
        String response = authUserService.registerUser(userDTO);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "User login", description = "Logs in a user and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        String token = authUserService.loginUser(loginDTO);
        return ResponseEntity.ok().body("{\"message\": \"Login successful!\", \"token\": \"" + token + "\"}");
    }

    @Operation(summary = "Forgot Password", description = "Allows users to reset their password if they forgot it.")
    @PutMapping("/forgotPassword/{email}")
    public ResponseEntity<String> forgotPassword(
            @PathVariable String email,
            @Valid @RequestBody Map<String, String> requestBody) {

        String newPassword = requestBody.get("password");
        String response = authUserService.forgotPassword(email, newPassword);
        return ResponseEntity.ok().body(response);
    }


    @Operation(summary = "Reset Password", description = "Allows a user to reset their password while logged in.")
    @PutMapping("/resetPassword/{email}")
    public ResponseEntity<String> resetPassword(
            @PathVariable String email,
            @RequestBody ResetPasswordDTO resetPasswordDTO) {
        String response = authUserService.resetPassword(email, resetPasswordDTO);
        return ResponseEntity.ok().body("{\"message\": \"" + response + "\"}");
    }
}
