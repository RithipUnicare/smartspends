package com.smartspend.smartspends.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.smartspend.smartspends.model.ExpenseCategory;
import com.smartspend.smartspends.model.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponseDTO {

    private Long id;
    private BigDecimal amount;
    private ExpenseCategory category;
    private PaymentMode paymentMode;
    private String description;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
}
