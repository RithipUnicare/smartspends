package com.smartspend.smartspends.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartspend.smartspends.dto.ApiResponse;
import com.smartspend.smartspends.dto.IncomeRequestDTO;
import com.smartspend.smartspends.dto.IncomeResponseDTO;
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.service.IncomeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> addIncome(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody IncomeRequestDTO request) {
        try {
            IncomeResponseDTO income = incomeService.addIncome(user, request);
            System.out.println("Added Income: " + income);
            return ResponseEntity.ok(new ApiResponse<>("Income added successfully", "SUCCESS", income));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to add income: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllIncomes(@AuthenticationPrincipal User user) {
        try {
            System.err.println("Fetching incomes for user: " + user.getUsername());
            List<IncomeResponseDTO> incomes = incomeService.getAllIncomes(user);
            System.out.println("Incomes: " + incomes);
            return ResponseEntity.ok(new ApiResponse<>("Incomes retrieved successfully", "SUCCESS", incomes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve incomes: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping("/month/{month}/year/{year}")
    public ResponseEntity<?> getIncomesByMonth(
            @AuthenticationPrincipal User user,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        try {
            List<IncomeResponseDTO> incomes = incomeService.getIncomesByMonth(user, month, year);
            return ResponseEntity.ok(new ApiResponse<>("Incomes retrieved successfully", "SUCCESS", incomes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve incomes: " + e.getMessage(), "ERROR", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIncome(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequestDTO request) {
        try {
            IncomeResponseDTO income = incomeService.updateIncome(id, user, request);
            return ResponseEntity.ok(new ApiResponse<>("Income updated successfully", "SUCCESS", income));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to update income: " + e.getMessage(), "ERROR", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        try {
            incomeService.deleteIncome(id, user);
            return ResponseEntity.ok(new ApiResponse<>("Income deleted successfully", "SUCCESS", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to delete income: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping("/current-month-total")
    public ResponseEntity<?> getCurrentMonthTotal(@AuthenticationPrincipal User user) {
        try {
            BigDecimal total = incomeService.getCurrentMonthTotalIncome(user);
            return ResponseEntity
                    .ok(new ApiResponse<>("Current month total retrieved successfully", "SUCCESS", total));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve total: " + e.getMessage(), "ERROR", null));
        }
    }
}
