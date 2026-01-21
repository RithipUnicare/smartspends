package com.smartspend.smartspends.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsResponseDTO {

    private BigDecimal currentSavings;
    private Double savingsPercentage;
    private List<String> recommendations;
    private List<String> suggestedSavingsOptions;
}
