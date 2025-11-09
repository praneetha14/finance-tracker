package com.finance.tracker.entity;

import com.finance.tracker.model.enums.MonthEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "monthly_expenses")
@Getter
@Setter
public class MonthlyExpenseEntity extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "month")
    private MonthEnum month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "cost")
    private double cost;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "expense_id")
    private ExpenseEntity expense;
}
