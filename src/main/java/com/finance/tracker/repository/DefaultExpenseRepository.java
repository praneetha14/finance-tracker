package com.finance.tracker.repository;

import com.finance.tracker.entity.DefaultExpenseEntity;
import com.finance.tracker.entity.ExpenseEntity;
import com.finance.tracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DefaultExpenseRepository extends JpaRepository<DefaultExpenseEntity, UUID> {
    Optional<DefaultExpenseEntity> findByUserAndExpenseType(UserEntity user, ExpenseEntity expense);
    List<DefaultExpenseEntity> findByUser(UserEntity user);
}
