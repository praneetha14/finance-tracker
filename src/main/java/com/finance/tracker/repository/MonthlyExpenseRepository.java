package com.finance.tracker.repository;

import com.finance.tracker.entity.ExpenseEntity;
import com.finance.tracker.entity.MonthlyExpenseEntity;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.model.enums.MonthEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MonthlyExpenseRepository extends JpaRepository<MonthlyExpenseEntity, UUID> {
    List<MonthlyExpenseEntity> findByUserAndMonth(UserEntity user, MonthEnum month);
    List<MonthlyExpenseEntity> findByUserAndMonthAndYear(UserEntity user, MonthEnum month, int year);
    MonthlyExpenseEntity findByUserAndExpenseAndMonth(UserEntity user, ExpenseEntity expenseEntity, MonthEnum monthEnum);
    boolean existsByUserAndMonthAndYear(UserEntity user, MonthEnum month, int year);

    UserEntity user(UserEntity user);
}
