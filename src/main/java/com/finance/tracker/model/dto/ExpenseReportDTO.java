package com.finance.tracker.model.dto;

import com.finance.tracker.model.enums.MonthEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseReportDTO {
    private String userId;
    private MonthEnum month;
    private Integer year;
    private double totalExpense;
    private double totalSaving;
    private String fileKey;
    private String fileUrl;
}
