package com.finance.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "expenses")
@Getter
@Setter
public class ExpenseEntity extends AbstractEntity {

    @Column(name = "expenses_type_name", nullable = false, unique = true)
    private String expenseTypeName;

}
