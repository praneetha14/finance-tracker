package com.finance.tracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyExpenseReportModel {

    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private double salary;
    private int month;
    private int year;
    private double previousMonthExpenses;
    private double previousMonthSavings;
    private double totalExpectedExpenses;
    private double totalDefaultExpenses;
    private double actualExpenses;
    private double percentageChange;
    private double totalExpectedSavings;
    private double actualSavings;
    private double percentageChangeSavings;
    private List<ExpenseItem> expenseItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpenseItem {
        private String expenseName;
        private double expectedCost;
        private String isDefault;
        private double actualCost;
        private double percentageChangeForCurrentMonth;
        private double previousMonthCost;
        private double percentageChangeFromPreviousMonth;
    }
}

