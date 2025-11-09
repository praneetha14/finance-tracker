package com.finance.tracker.model.vo;

import com.finance.tracker.model.dto.ExpenseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseSummaryVO {
    private List<ExpenseDTO> defaultExpenses;
    private List<ExpenseDTO> monthlyExpenses;
    private double totalDefaultExpenses;
    private double otherExpenses;
    private double totalExpenses;
}
