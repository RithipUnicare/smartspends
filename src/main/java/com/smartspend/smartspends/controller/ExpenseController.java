package com.smartspend.smartspends.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartspend.smartspends.dto.ApiResponse;
import com.smartspend.smartspends.dto.ExpenseRequestDTO;
import com.smartspend.smartspends.dto.ExpenseResponseDTO;
import com.smartspend.smartspends.model.ExpenseCategory;
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> addExpense(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ExpenseRequestDTO request) {
        try {
            ExpenseResponseDTO expense = expenseService.addExpense(user, request);
            return ResponseEntity.ok(new ApiResponse<>("Expense added successfully", "SUCCESS", expense));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to add expense: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllExpenses(@AuthenticationPrincipal User user) {
        try {
            List<ExpenseResponseDTO> expenses = expenseService.getAllExpenses(user);
            return ResponseEntity.ok(new ApiResponse<>("Expenses retrieved successfully", "SUCCESS", expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve expenses: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<?> getExpensesByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam String start,
            @RequestParam String end) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime startDate = LocalDateTime.parse(start, formatter);
            LocalDateTime endDate = LocalDateTime.parse(end, formatter);

            List<ExpenseResponseDTO> expenses = expenseService.getExpensesByDateRange(user, startDate, endDate);
            return ResponseEntity.ok(new ApiResponse<>("Expenses retrieved successfully", "SUCCESS", expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve expenses: " + e.getMessage(), "ERROR", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequestDTO request) {
        try {
            ExpenseResponseDTO expense = expenseService.updateExpense(id, user, request);
            return ResponseEntity.ok(new ApiResponse<>("Expense updated successfully", "SUCCESS", expense));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to update expense: " + e.getMessage(), "ERROR", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        try {
            expenseService.deleteExpense(id, user);
            return ResponseEntity.ok(new ApiResponse<>("Expense deleted successfully", "SUCCESS", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to delete expense: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getExpensesByCategory(
            @AuthenticationPrincipal User user,
            @PathVariable ExpenseCategory category) {
        try {
            // This would need a service method, for now return all and filter client-side
            List<ExpenseResponseDTO> expenses = expenseService.getAllExpenses(user);
            return ResponseEntity.ok(new ApiResponse<>("Expenses retrieved successfully", "SUCCESS", expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve expenses: " + e.getMessage(), "ERROR", null));
        }
    }

    @GetMapping("/current-month-total")
    public ResponseEntity<?> getCurrentMonthTotal(@AuthenticationPrincipal User user) {
        try {
            BigDecimal total = expenseService.getCurrentMonthTotalExpense(user);
            return ResponseEntity
                    .ok(new ApiResponse<>("Current month total retrieved successfully", "SUCCESS", total));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Failed to retrieve total: " + e.getMessage(), "ERROR", null));
        }
    }
}
