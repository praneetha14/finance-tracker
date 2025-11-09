package com.finance.tracker.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseTypeVO {
    private UUID id;
    private String expenseTypeName;
    private boolean isDefault;
}
