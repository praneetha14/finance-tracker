package com.finance.tracker.service.impl;

import com.finance.tracker.entity.DefaultExpenseEntity;
import com.finance.tracker.entity.MonthlyExpenseEntity;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.model.dto.ExpenseDTO;
import com.finance.tracker.model.enums.MonthEnum;
import com.finance.tracker.model.vo.ExpenseSummaryVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.repository.DefaultExpenseRepository;
import com.finance.tracker.repository.MonthlyExpenseRepository;
import com.finance.tracker.service.MonthlyExpenseService;
import com.finance.tracker.service.utils.AuthenticationUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MonthlyExpenseServiceImpl class is the Implementation of MonthlyExpenseService that retrieves and summarizes
 * monthly expenses for authenticated users.
 * Combines both default and custom expenses to generate a consolidated monthly expense summary including totals and
 * breakdown details.
 */
@RequiredArgsConstructor
public class MonthlyExpenseServiceImpl implements MonthlyExpenseService {

    private final MonthlyExpenseRepository monthlyExpenseRepository;
    private final DefaultExpenseRepository defaultExpenseRepository;
    private final AuthenticationUtils authenticationUtils;

    /**
     * Retrieves a summary of the user's monthly expenses.
     * The method includes: All default recurring expenses. All manually added monthly expenses.
     * Breakdown of total default, additional, and overall expenses.
     *
     * @param month  the month for which the expenses are requested (1–12)
     * @param year   the year for which the expenses are requested
     * @param apiKey the user's API key for authentication
     * @return a SuccessResponseVO containing the ExpenseSummaryVO with detailed expense breakdown and total calculations.
     */
    @Override
    public SuccessResponseVO<ExpenseSummaryVO> getMonthlyExpense(int month, int year, String apiKey) {
        UserEntity userEntity = authenticationUtils.getCurrentUser(apiKey);
        MonthEnum monthEnum = MonthEnum.fromNumber(month);
        List<DefaultExpenseEntity> defaultExpenses = defaultExpenseRepository.findByUser(userEntity);
        List<MonthlyExpenseEntity> monthlyExpenses = monthlyExpenseRepository.findByUserAndMonthAndFinancialYear(userEntity, monthEnum, year);
        List<ExpenseDTO> defaultExpenseDTOs = defaultExpenses.stream()
                .map(exp -> new ExpenseDTO(
                        exp.getExpenseType().getExpenseTypeName(),
                        exp.getAmount(),
                        month))
                .collect(Collectors.toList());
        List<ExpenseDTO> expenseDTOS = monthlyExpenses.stream()
                .map(exp -> new ExpenseDTO(
                        exp.getExpense().getExpenseTypeName(),
                        exp.getCost(),
                        month))
                .toList();
        double totalDefaultExpenses = defaultExpenses.stream()
                .mapToDouble(DefaultExpenseEntity::getAmount)
                .sum();
        double totalMonthlyExpenses = monthlyExpenses.stream()
                .mapToDouble(MonthlyExpenseEntity::getCost)
                .sum();
        totalMonthlyExpenses -= totalDefaultExpenses;
        double totalExpenses = totalDefaultExpenses + totalMonthlyExpenses;
        ExpenseSummaryVO expenseSummaryVO = new ExpenseSummaryVO(
                defaultExpenseDTOs,
                expenseDTOS,
                totalDefaultExpenses,
                totalMonthlyExpenses,
                totalExpenses
        );
        return SuccessResponseVO.of(200, "Monthly expenses summary retrieved successfully", expenseSummaryVO);
    }
}
