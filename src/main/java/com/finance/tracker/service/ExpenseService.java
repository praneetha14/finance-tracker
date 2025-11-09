package com.finance.tracker.service;

import com.finance.tracker.model.dto.ExpenseDTO;
import com.finance.tracker.model.vo.CreateResponseVO;
import com.finance.tracker.model.vo.SuccessResponseVO;

public interface ExpenseService {

    SuccessResponseVO<CreateResponseVO> createExpense(ExpenseDTO expenseDTO, boolean isDefault, String apiKey);
    SuccessResponseVO<CreateResponseVO> updateDefaultExpense(String expenseName,double newAmount, String apiKey);
}
