package com.finance.tracker.repository;

import com.finance.tracker.entity.ExpenseReportEntity;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.model.enums.MonthEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExpenseReportRepository extends JpaRepository<ExpenseReportEntity, UUID> {
    Optional<ExpenseReportEntity> findByUserAndMonthAndFinancialYear(UserEntity user, MonthEnum month, int year);
}
