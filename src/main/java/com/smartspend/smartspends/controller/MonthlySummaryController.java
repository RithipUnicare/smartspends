package com.smartspend.smartspends.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartspend.smartspends.dto.ApiResponse;
import com.smartspend.smartspends.dto.MonthlySummaryDTO;
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.service.MonthlySummaryService;

@RestController
@RequestMapping("/api/monthly-summary")
public class MonthlySummaryController {

    @Autowired
    private MonthlySummaryService monthlySummaryService;

    @GetMapping
    public ResponseEntity<?> getAllSummaries(@AuthenticationPrincipal User user) {
        try {
            List<MonthlySummaryDTO> summaries = monthlySummaryService.getAllSummaries(user);
            return ResponseEntity
                    .ok(new ApiResponse<>("Monthly summaries retrieved successfully", "SUCCESS", summaries));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve summaries: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping("/{month}/{year}")
    public ResponseEntity<?> getMonthlySummary(
            @AuthenticationPrincipal User user,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        try {
            MonthlySummaryDTO summary = monthlySummaryService.getMonthlySummary(user, month, year);
            if (summary == null) {
                return ResponseEntity.ok(
                        new ApiResponse<>("No summary found for this month", "SUCCESS", null));
            }
            return ResponseEntity.ok(new ApiResponse<>("Monthly summary retrieved successfully", "SUCCESS", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve summary: " + e.getMessage(), "ERROR", null));
        }
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calculateCurrentMonthSummary(@AuthenticationPrincipal User user) {
        try {
            LocalDate now = LocalDate.now();
            MonthlySummaryDTO summary = monthlySummaryService.calculateMonthlySummary(user, now.getMonthValue(),
                    now.getYear());
            return ResponseEntity.ok(new ApiResponse<>("Monthly summary calculated successfully", "SUCCESS", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to calculate summary: " + e.getMessage(), "ERROR", null));
        }
    }
}
