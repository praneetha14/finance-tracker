package com.finance.tracker.service;

import com.finance.tracker.model.vo.ExpenseSummaryVO;
import com.finance.tracker.model.vo.SuccessResponseVO;

public interface MonthlyExpenseService {
    SuccessResponseVO<ExpenseSummaryVO> getMonthlyExpense(int month, int year, String apiKey);
}
