package com.finance.tracker.service;

import com.finance.tracker.model.dto.MonthlyExpenseReportModel;

public interface PdfService {
    byte[] generateMonthlyExpenseReport(MonthlyExpenseReportModel reportModel);
}
