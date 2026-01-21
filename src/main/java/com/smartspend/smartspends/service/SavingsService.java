package com.smartspend.smartspends.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartspend.smartspends.dto.SavingsResponseDTO;
import com.smartspend.smartspends.model.User;

@Service
public class SavingsService {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private ExpenseService expenseService;

    public SavingsResponseDTO getSavingsRecommendations(User user) {
        BigDecimal currentIncome = incomeService.getCurrentMonthTotalIncome(user);
        BigDecimal currentExpense = expenseService.getCurrentMonthTotalExpense(user);
        BigDecimal currentSavings = currentIncome.subtract(currentExpense);

        double savingsPercentage = calculateSavingsPercentage(currentIncome, currentExpense);

        List<String> recommendations = generateRecommendations(user, currentIncome, currentExpense, savingsPercentage);
        List<String> savingsOptions = generateSavingsOptions(currentSavings, savingsPercentage);

        return new SavingsResponseDTO(
                currentSavings,
                savingsPercentage,
                recommendations,
                savingsOptions);
    }

    public double calculateSavingsPercentage(BigDecimal income, BigDecimal expense) {
        if (income.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        BigDecimal savings = income.subtract(expense);
        BigDecimal percentage = savings.divide(income, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        return percentage.doubleValue();
    }

    private List<String> generateRecommendations(User user, BigDecimal income, BigDecimal expense,
            double savingsPercentage) {
        List<String> recommendations = new ArrayList<>();

        // Analyze savings percentage
        if (savingsPercentage < 10) {
            recommendations.add("⚠️ Your savings rate is below 10%. Try to reduce discretionary expenses.");
        } else if (savingsPercentage < 20) {
            recommendations.add("💡 Good start! Aim for at least 20% savings for financial security.");
        } else if (savingsPercentage >= 20 && savingsPercentage < 30) {
            recommendations.add("👍 Great job! You're saving 20-30% of your income.");
        } else {
            recommendations.add("🌟 Excellent! You're saving over 30% of your income.");
        }

        // Analyze miscellaneous expenses
        LocalDate now = LocalDate.now();
        Map<String, BigDecimal> categoryExpenses = expenseService.getCategoryWiseExpenses(user, now.getMonthValue(),
                now.getYear());
        BigDecimal miscExpense = categoryExpenses.getOrDefault("MISCELLANEOUS", BigDecimal.ZERO);

        if (miscExpense.compareTo(BigDecimal.ZERO) > 0) {
            double miscPercentage = miscExpense.divide(expense, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100")).doubleValue();
            if (miscPercentage > 15) {
                recommendations.add(
                        String.format("📊 Miscellaneous expenses are %.1f%% of total. Track small expenses carefully.",
                                miscPercentage));
            }
        }

        // Check high expense categories
        for (Map.Entry<String, BigDecimal> entry : categoryExpenses.entrySet()) {
            if (!entry.getKey().equals("MISCELLANEOUS") && entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                double categoryPercentage = entry.getValue().divide(expense, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")).doubleValue();
                if (categoryPercentage > 30) {
                    recommendations.add(
                            String.format("💰 %s expenses are %.1f%% of total. Consider ways to reduce this category.",
                                    entry.getKey(), categoryPercentage));
                }
            }
        }

        // Income vs Expense analysis
        if (expense.compareTo(income) > 0) {
            recommendations.add("🚨 Alert: Your expenses exceed your income! Immediate action required.");
        }

        return recommendations;
    }

    private List<String> generateSavingsOptions(BigDecimal savings, double savingsPercentage) {
        List<String> options = new ArrayList<>();

        if (savings.compareTo(BigDecimal.ZERO) <= 0) {
            options.add("Focus on reducing expenses before considering savings instruments");
            return options;
        }

        if (savings.compareTo(new BigDecimal("5000")) >= 0) {
            options.add("💰 Consider a Recurring Deposit (RD) for regular savings");
            options.add("🏦 Open a High-Interest Savings Account");
        }

        if (savings.compareTo(new BigDecimal("10000")) >= 0) {
            options.add("📈 Start a Systematic Investment Plan (SIP) in mutual funds");
            options.add("💼 Consider Fixed Deposits for guaranteed returns");
        }

        if (savings.compareTo(new BigDecimal("25000")) >= 0) {
            options.add("🎯 Explore diversified investment portfolios");
            options.add("🏡 Consider long-term investment options like PPF or NPS");
        }

        if (savingsPercentage > 30) {
            options.add("✨ With excellent savings rate, explore wealth creation opportunities");
        }

        return options;
    }
}
