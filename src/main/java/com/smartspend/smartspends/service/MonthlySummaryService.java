package com.smartspend.smartspends.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartspend.smartspends.dto.MonthlySummaryDTO;
import com.smartspend.smartspends.model.MonthlySummary;
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.repository.MonthlySummaryRepository;
import com.smartspend.smartspends.repository.UserRepository;

@Service
public class MonthlySummaryService {

    @Autowired
    private MonthlySummaryRepository monthlySummaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ObjectMapper objectMapper;

    public MonthlySummaryDTO calculateMonthlySummary(User user, Integer month, Integer year) {
        // Calculate totals
        List<com.smartspend.smartspends.dto.IncomeResponseDTO> incomes = incomeService.getIncomesByMonth(user, month,
                year);
        BigDecimal totalIncome = incomes.stream()
                .map(com.smartspend.smartspends.dto.IncomeResponseDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> categoryExpenses = expenseService.getCategoryWiseExpenses(user, month, year);
        BigDecimal totalExpense = categoryExpenses.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSavings = totalIncome.subtract(totalExpense);

        // Convert category expenses to JSON string
        String categoryJson;
        try {
            categoryJson = objectMapper.writeValueAsString(categoryExpenses);
        } catch (JsonProcessingException e) {
            categoryJson = "{}";
        }

        // Save or update summary
        MonthlySummary summary = monthlySummaryRepository
                .findByUserAndMonthAndYear(user, month, year)
                .orElse(new MonthlySummary());

        summary.setUser(user);
        summary.setMonth(month);
        summary.setYear(year);
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpense(totalExpense);
        summary.setTotalSavings(totalSavings);
        summary.setCategoryWiseExpense(categoryJson);
        summary.setCalculatedAt(LocalDateTime.now());

        MonthlySummary savedSummary = monthlySummaryRepository.save(summary);
        return mapToDTO(savedSummary);
    }

    public List<MonthlySummaryDTO> getAllSummaries(User user) {
        return monthlySummaryRepository.findByUserOrderByYearDescMonthDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MonthlySummaryDTO getMonthlySummary(User user, Integer month, Integer year) {
        return monthlySummaryRepository.findByUserAndMonthAndYear(user, month, year)
                .map(this::mapToDTO)
                .orElse(null);
    }

    // Run at 1 AM on the first day of every month
    @Scheduled(cron = "0 0 1 1 * ?")
    public void generateMonthlySummariesForAllUsers() {
        LocalDate now = LocalDate.now();
        YearMonth previousMonth = YearMonth.of(now.getYear(), now.getMonthValue()).minusMonths(1);

        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            try {
                calculateMonthlySummary(user, previousMonth.getMonthValue(), previousMonth.getYear());
            } catch (Exception e) {
                // Log error but continue processing other users
                System.err.println("Error calculating summary for user " + user.getId() + ": " + e.getMessage());
            }
        }
    }

    private MonthlySummaryDTO mapToDTO(MonthlySummary summary) {
        Map<String, BigDecimal> categoryExpenses;
        try {
            categoryExpenses = objectMapper.readValue(summary.getCategoryWiseExpense(),
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, BigDecimal.class));
        } catch (Exception e) {
            categoryExpenses = Map.of();
        }

        return new MonthlySummaryDTO(
                summary.getId(),
                summary.getMonth(),
                summary.getYear(),
                summary.getTotalIncome(),
                summary.getTotalExpense(),
                summary.getTotalSavings(),
                categoryExpenses);
    }
}
