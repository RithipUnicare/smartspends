package com.smartspend.smartspends.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartspend.smartspends.dto.IncomeRequestDTO;
import com.smartspend.smartspends.dto.IncomeResponseDTO;
import com.smartspend.smartspends.model.Income;
import com.smartspend.smartspends.model.User;
import com.smartspend.smartspends.repository.IncomeRepository;

@Service
public class IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    public IncomeResponseDTO addIncome(User user, IncomeRequestDTO request) {
        Income income = new Income();
        income.setUser(user);
        income.setAmount(request.getAmount());
        income.setSource(request.getSource());
        income.setMonth(request.getMonth());
        income.setYear(request.getYear());
        income.setDescription(request.getDescription());

        Income savedIncome = incomeRepository.save(income);
        return mapToResponseDTO(savedIncome);
    }

    public List<IncomeResponseDTO> getAllIncomes(User user) {
        return incomeRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<IncomeResponseDTO> getIncomesByMonth(User user, Integer month, Integer year) {
        return incomeRepository.findByUserAndMonthAndYear(user, month, year)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public IncomeResponseDTO updateIncome(Long id, User user, IncomeRequestDTO request) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        if (!income.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to income");
        }

        income.setAmount(request.getAmount());
        income.setSource(request.getSource());
        income.setMonth(request.getMonth());
        income.setYear(request.getYear());
        income.setDescription(request.getDescription());

        Income updatedIncome = incomeRepository.save(income);
        return mapToResponseDTO(updatedIncome);
    }

    public void deleteIncome(Long id, User user) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        if (!income.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to income");
        }

        incomeRepository.delete(income);
    }

    public BigDecimal getCurrentMonthTotalIncome(User user) {
        LocalDate now = LocalDate.now();
        List<Income> incomes = incomeRepository.findByUserAndMonthAndYear(user, now.getMonthValue(), now.getYear());
        
        return incomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private IncomeResponseDTO mapToResponseDTO(Income income) {
        return new IncomeResponseDTO(
                income.getId(),
                income.getAmount(),
                income.getSource(),
                income.getMonth(),
                income.getYear(),
                income.getDescription(),
                income.getCreatedAt());
    }
}
