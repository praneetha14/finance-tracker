package com.finance.tracker.repository;

import com.finance.tracker.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, UUID> {
    ExpenseEntity findByExpenseTypeName(String expenseTypeName);
}
