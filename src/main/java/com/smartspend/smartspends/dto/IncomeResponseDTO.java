package com.smartspend.smartspends.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResponseDTO {

    private Long id;
    private BigDecimal amount;
    private String source;
    private Integer month;
    private Integer year;
    private String description;
    private LocalDateTime createdAt;
}
