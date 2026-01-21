package com.smartspend.smartspends.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.smartspend.smartspends.dto.ExpenseRequestDTO;
import com.smartspend.smartspends.dto.ExpenseResponseDTO;
import com.smartspend.smartspends.model.Expense;
import com.smartspend.smartspends.model.ExpenseCategory;
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.repository.ExpenseRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public ExpenseResponseDTO addExpense(User user, ExpenseRequestDTO request) {
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setPaymentMode(request.getPaymentMode());
        expense.setDescription(request.getDescription());
        // transactionDate is auto-set by @PrePersist in Expense entity

        Expense savedExpense = expenseRepository.save(expense);
        return mapToResponseDTO(savedExpense);
    }

    public List<ExpenseResponseDTO> getAllExpenses(User user) {
        return expenseRepository.findByUser(user)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ExpenseResponseDTO> getExpensesByDateRange(User user, LocalDateTime start, LocalDateTime end) {
        return expenseRepository.findByUserAndTransactionDateBetween(user, start, end)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ExpenseResponseDTO> getRecentExpenses(User user, int limit) {
        return expenseRepository.findByUserOrderByTransactionDateDesc(user, PageRequest.of(0, limit))
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ExpenseResponseDTO updateExpense(Long id, User user, ExpenseRequestDTO request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to expense");
        }

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setPaymentMode(request.getPaymentMode());
        expense.setDescription(request.getDescription());

        Expense updatedExpense = expenseRepository.save(expense);
        return mapToResponseDTO(updatedExpense);
    }

    public void deleteExpense(Long id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to expense");
        }

        expenseRepository.delete(expense);
    }

    public Map<String, BigDecimal> getCategoryWiseExpenses(User user, Integer month, Integer year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(user, start, end);

        Map<String, BigDecimal> categoryExpenses = new HashMap<>();
        for (ExpenseCategory category : ExpenseCategory.values()) {
            categoryExpenses.put(category.name(), BigDecimal.ZERO);
        }

        for (Expense expense : expenses) {
            String categoryName = expense.getCategory().name();
            BigDecimal currentAmount = categoryExpenses.get(categoryName);
            categoryExpenses.put(categoryName, currentAmount.add(expense.getAmount()));
        }

        return categoryExpenses;
    }

    public BigDecimal getCurrentMonthTotalExpense(User user) {
        LocalDate now = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(now.getYear(), now.getMonthValue());
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(user, start, end);

        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ExpenseResponseDTO mapToResponseDTO(Expense expense) {
        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getPaymentMode(),
                expense.getDescription(),
                expense.getTransactionDate(),
                expense.getCreatedAt());
    }
}
