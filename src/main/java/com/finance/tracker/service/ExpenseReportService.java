package com.finance.tracker.service;

import com.finance.tracker.model.dto.SavingsDTO;
import com.finance.tracker.model.vo.FileReportVO;
import com.finance.tracker.model.vo.FinancesVO;
import com.finance.tracker.model.vo.SuccessResponseVO;

public interface ExpenseReportService {

    SuccessResponseVO<FinancesVO> submitFinances(SavingsDTO savingsDTO, String apiKey);
    SuccessResponseVO<FileReportVO> getExpenseReport(int month, int year, String apiKey);
}
