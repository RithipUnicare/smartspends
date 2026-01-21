package com.smartspend.smartspends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartspend.smartspends.model.Income;
import com.smartspend.smartspends.model.User;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserAndMonthAndYear(User user, Integer month, Integer year);

    List<Income> findByUser(User user);

    List<Income> findByUserOrderByCreatedAtDesc(User user);
}
