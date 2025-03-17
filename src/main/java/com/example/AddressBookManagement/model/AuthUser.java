package com.example.AddressBookManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "First letter must be uppercase")
    private String firstName;

    @NotBlank
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "First letter must be uppercase")
    private String lastName;

    @NotBlank
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Column(length = 512) // Store reset token
    private String resetToken;

    private Date tokenExpiry; // Store token expiration time
}
