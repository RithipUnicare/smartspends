package com.smartspend.smartspends.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonDTO {
    
    private BigDecimal previousMonthIncome;
    private BigDecimal previousMonthExpense;
    private BigDecimal previousMonthSavings;
    private Double incomePercentageChange;
    private Double expensePercentageChange;
    private Double savingsPercentageChange;
}
