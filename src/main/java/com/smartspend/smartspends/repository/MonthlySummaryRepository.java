package com.smartspend.smartspends.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.smartspend.smartspends.model.MonthlySummary;
import com.smartspend.smartspends.model.User;

import java.util.List;

public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, Long> {

    Optional<MonthlySummary> findByUserAndMonthAndYear(User user, Integer month, Integer year);

    List<MonthlySummary> findByUserOrderByYearDescMonthDesc(User user, Pageable pageable);
    
    List<MonthlySummary> findByUserOrderByYearDescMonthDesc(User user);
}
