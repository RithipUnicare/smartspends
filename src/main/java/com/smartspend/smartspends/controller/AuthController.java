package com.smartspend.smartspends.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartspend.smartspends.config.JwtUtil;
import com.smartspend.smartspends.dto.ApiResponse;
import com.smartspend.smartspends.dto.ErrorResponse;
import com.smartspend.smartspends.dto.LoginRequestDTO;
import com.smartspend.smartspends.dto.LoginResponseDTO;
import com.smartspend.smartspends.dto.RegistrationRequestDTO;
import com.smartspend.smartspends.dto.RegistrationResponseDTO;
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequestDTO registrationRequest) {
        if (userRepository.existsByMobileNumber(registrationRequest.getMobileNumber())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Error", "Mobile number already registered", "MOBILE_ALREADY_EXISTS"));
        }

        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Error", "Username already taken", "USERNAME_ALREADY_EXISTS"));
        }

        // Create new user entity
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setMobileNumber(registrationRequest.getMobileNumber());
        user.setEmail(registrationRequest.getEmail());
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        // Create response DTO
        RegistrationResponseDTO response = new RegistrationResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getMobileNumber(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "User registered successfully");

        return ResponseEntity.ok().body(new ApiResponse<>("User registered successfully", "SUCCESS", response));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getMobileNumber(),
                            loginRequest.getPassword()));

            // Generate JWT token
            String token = jwtUtil.generateToken(loginRequest.getMobileNumber());

            // Fetch user details
            User user = userRepository.findByMobileNumber(loginRequest.getMobileNumber())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create response DTO
            LoginResponseDTO response = new LoginResponseDTO(
                    token,
                    user.getMobileNumber(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    "Login successful");

            return ResponseEntity.ok(new ApiResponse<>("Login successful", "SUCCESS", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(401))
                    .body(new ErrorResponse("Error", "Invalid mobile number or password", "INVALID_CREDENTIALS"));
        }
    }

}