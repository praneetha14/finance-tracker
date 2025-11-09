package com.finance.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "default_expenses")
@Getter
@Setter
public class DefaultExpenseEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "expense_type_id", nullable = false)
    private ExpenseEntity expenseType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "amount")
    private double amount;
}
