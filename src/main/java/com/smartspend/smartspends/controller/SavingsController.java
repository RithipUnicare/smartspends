package com.smartspend.smartspends.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartspend.smartspends.dto.ApiResponse;
import com.smartspend.smartspends.dto.SavingsResponseDTO;
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.service.SavingsService;

@RestController
@RequestMapping("/api/savings")
public class SavingsController {

    @Autowired
    private SavingsService savingsService;

    @GetMapping
    public ResponseEntity<?> getSavings(@AuthenticationPrincipal User user) {
        try {
            SavingsResponseDTO savings = savingsService.getSavingsRecommendations(user);
            return ResponseEntity
                    .ok(new ApiResponse<>("Savings data retrieved successfully", "SUCCESS", savings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve savings: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(@AuthenticationPrincipal User user) {
        try {
            SavingsResponseDTO savings = savingsService.getSavingsRecommendations(user);
            return ResponseEntity.ok(
                    new ApiResponse<>("Recommendations retrieved successfully", "SUCCESS",
                            savings.getRecommendations()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve recommendations: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping("/analysis")
    public ResponseEntity<?> getAnalysis(@AuthenticationPrincipal User user) {
        try {
            SavingsResponseDTO savings = savingsService.getSavingsRecommendations(user);
            return ResponseEntity.ok(new ApiResponse<>("Analysis retrieved successfully", "SUCCESS", savings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve analysis: " + e.getMessage(), "ERROR", null));
        }
    }
}
