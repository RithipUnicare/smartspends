package com.smartspend.smartspends.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.smartspend.smartspends.model.Expense;
import com.smartspend.smartspends.model.ExpenseCategory;
import com.smartspend.smartspends.model.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserAndTransactionDateBetween(User user, LocalDateTime start, LocalDateTime end);

    List<Expense> findByUser(User user);

    List<Expense> findByUserOrderByTransactionDateDesc(User user, Pageable pageable);

    List<Expense> findByUserAndCategory(User user, ExpenseCategory category);
}
