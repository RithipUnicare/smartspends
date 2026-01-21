package com.smartspend.smartspends.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {

    private BigDecimal currentMonthIncome;
    private BigDecimal currentMonthExpense;
    private BigDecimal currentMonthSavings;
    private Map<String, BigDecimal> categoryExpenses;
    private ComparisonDTO previousMonthComparison;
    private List<ExpenseResponseDTO> recentExpenses;
}
