package com.finance.tracker.entity;

import com.finance.tracker.model.enums.MonthEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "expense_reports")
@Getter
@Setter
public class ExpenseReportEntity extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "month_name", nullable = false)
    private MonthEnum month;

    @Column(name = "financial_year", nullable = false)
    private Integer financialYear;

    @Column(name = "file_key", unique = true)
    private String fileKey;

    @Column(name = "total_expense")
    private double totalExpense;

    @Column(name = "total_saving")
    private double totalSaving;

    @Column(name = "expected_expenses")
    private double expectedExpenses;

    @Column(name = "estimated_savings")
    private double estimatedSavings;
}
