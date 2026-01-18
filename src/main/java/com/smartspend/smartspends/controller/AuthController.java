package com.smartspend.smartspends.controller;

import java.util.Map;

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
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.repository.UserRepository;

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
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByMobileNumber(user.getMobileNumber())) {

            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Error", "Mobile number already registered", "MOBILE_ALREADY_EXISTS"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok().body(new ApiResponse<>("User registered successfully", "SUCCESS", null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getMobileNumber(), user.getPassword()));

            String token = jwtUtil.generateToken(user.getMobileNumber());

            return ResponseEntity.ok(new ApiResponse<>("Login successful", "SUCCESS", Map.of("token", token)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(401))
                    .body(new ErrorResponse("Error", "Invalid mobile number or password", "INVALID_CREDENTIALS"));
        }

    }

}