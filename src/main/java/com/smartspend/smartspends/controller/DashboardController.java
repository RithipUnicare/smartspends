package com.smartspend.smartspends.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartspend.smartspends.dto.ApiResponse;
import com.smartspend.smartspends.dto.ComparisonDTO;
import com.smartspend.smartspends.dto.DashboardResponseDTO;
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal User user) {
        try {
            DashboardResponseDTO dashboard = dashboardService.getDashboardData(user);
            return ResponseEntity.ok(new ApiResponse<>("Dashboard data retrieved successfully", "SUCCESS", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve dashboard: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping("/comparison")
    public ResponseEntity<?> getMonthlyComparison(@AuthenticationPrincipal User user) {
        try {
            ComparisonDTO comparison = dashboardService.getMonthlyComparison(user);
            return ResponseEntity
                    .ok(new ApiResponse<>("Comparison data retrieved successfully", "SUCCESS", comparison));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve comparison: " + e.getMessage(), "ERROR", null));
        }
    }
}
