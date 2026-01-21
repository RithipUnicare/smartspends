package com.smartspend.smartspends.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "monthly_summaries")
@Data
public class MonthlySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer month; // 1-12

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalIncome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalExpense;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSavings;

    @Column(columnDefinition = "TEXT")
    private String categoryWiseExpense; // JSON format

    @Column(nullable = false)
    private LocalDateTime calculatedAt;
}
