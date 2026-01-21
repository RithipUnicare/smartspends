package com.smartspend.smartspends.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartspend.smartspends.dto.ComparisonDTO;
import com.smartspend.smartspends.dto.DashboardResponseDTO;
import com.smartspend.smartspends.dto.ExpenseResponseDTO;
import com.smartspend.smartspends.model.MonthlySummary;
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.repository.MonthlySummaryRepository;

@Service
public class DashboardService {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private MonthlySummaryRepository monthlySummaryRepository;

    public DashboardResponseDTO getDashboardData(User user) {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // Get current month data
        BigDecimal currentMonthIncome = incomeService.getCurrentMonthTotalIncome(user);
        BigDecimal currentMonthExpense = expenseService.getCurrentMonthTotalExpense(user);
        BigDecimal currentMonthSavings = currentMonthIncome.subtract(currentMonthExpense);

        // Get category breakdown
        Map<String, BigDecimal> categoryExpenses = expenseService.getCategoryWiseExpenses(user, currentMonth,
                currentYear);

        // Get recent expenses
        List<ExpenseResponseDTO> recentExpenses = expenseService.getRecentExpenses(user, 10);

        // Get monthly comparison
        ComparisonDTO comparison = getMonthlyComparison(user);

        return new DashboardResponseDTO(
                currentMonthIncome,
                currentMonthExpense,
                currentMonthSavings,
                categoryExpenses,
                comparison,
                recentExpenses);
    }

    public ComparisonDTO getMonthlyComparison(User user) {
        LocalDate now = LocalDate.now();
        YearMonth previousMonth = YearMonth.of(now.getYear(), now.getMonthValue()).minusMonths(1);

        // Try to get previous month summary from database
        MonthlySummary previousSummary = monthlySummaryRepository
                .findByUserAndMonthAndYear(user, previousMonth.getMonthValue(), previousMonth.getYear())
                .orElse(null);

        BigDecimal currentMonthIncome = incomeService.getCurrentMonthTotalIncome(user);
        BigDecimal currentMonthExpense = expenseService.getCurrentMonthTotalExpense(user);
        BigDecimal currentMonthSavings = currentMonthIncome.subtract(currentMonthExpense);

        if (previousSummary == null) {
            // No previous data, return null comparison
            return new ComparisonDTO(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    0.0,
                    0.0,
                    0.0);
        }

        BigDecimal previousIncome = previousSummary.getTotalIncome();
        BigDecimal previousExpense = previousSummary.getTotalExpense();
        BigDecimal previousSavings = previousSummary.getTotalSavings();

        double incomeChange = calculatePercentageChange(previousIncome, currentMonthIncome);
        double expenseChange = calculatePercentageChange(previousExpense, currentMonthExpense);
        double savingsChange = calculatePercentageChange(previousSavings, currentMonthSavings);

        return new ComparisonDTO(
                previousIncome,
                previousExpense,
                previousSavings,
                incomeChange,
                expenseChange,
                savingsChange);
    }

    private double calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return newValue.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        BigDecimal change = newValue.subtract(oldValue);
        BigDecimal percentageChange = change.divide(oldValue, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
        return percentageChange.doubleValue();
    }
}
