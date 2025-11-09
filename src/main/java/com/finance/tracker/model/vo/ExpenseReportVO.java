package com.finance.tracker.model.vo;

import com.finance.tracker.model.enums.MonthEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseReportVO {
    private UUID id;
    private MonthEnum month;
    private Integer year;
    private String fileKey;
    private String fileUrl;
    private double totalExpense;
    private double totalSaving;
}
