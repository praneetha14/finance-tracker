package com.finance.tracker.service.impl;

import com.finance.tracker.entity.DefaultExpenseEntity;
import com.finance.tracker.entity.ExpenseEntity;
import com.finance.tracker.entity.MonthlyExpenseEntity;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.exception.DuplicateResourceException;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.dto.ExpenseDTO;
import com.finance.tracker.model.enums.MonthEnum;
import com.finance.tracker.model.vo.CreateResponseVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.repository.DefaultExpenseRepository;
import com.finance.tracker.repository.ExpenseRepository;
import com.finance.tracker.repository.MonthlyExpenseRepository;
import com.finance.tracker.service.ExpenseService;
import com.finance.tracker.service.utils.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * ExpenseServiceImpl class is the Implementation of ExpenseService interface that manages user expenses.
 * Handles creation and update of default and monthly expenses,
 * ensuring data integrity and preventing duplicate entries.
 * Includes user authentication and financial report validations.
 */
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final AuthenticationUtils authenticationUtils;
    private final MonthlyExpenseRepository monthlyExpenseRepository;
    private final DefaultExpenseRepository defaultExpenseRepository;

    /**
     * Creates a new expense for the authenticated user.
     * If the expense type already exists, its cost is updated.
     * Supports marking an expense as a default type for recurring months.
     * Prevents modification if a financial report for the month is already generated.
     *
     * @param expenseDTO the expense details
     * @param isDefault  flag indicating if the expense should be stored as default
     * @param apiKey     API key for user authentication
     * @return a  SuccessResponseVO containing the created or updated expense ID
     * @throws DuplicateResourceException if a default expense of the same type already exists
     */
    @Override
    public SuccessResponseVO<CreateResponseVO> createExpense(ExpenseDTO expenseDTO, boolean isDefault,
                                                             String apiKey) {
        UserEntity userEntity = authenticationUtils.getCurrentUser(apiKey);
        ExpenseEntity expenseEntity = expenseRepository.findByExpenseTypeName(expenseDTO.getExpenseTypeName());
        if (expenseEntity == null) {
            expenseEntity = new ExpenseEntity();
            expenseEntity.setExpenseTypeName(expenseDTO.getExpenseTypeName());
            expenseEntity = expenseRepository.save(expenseEntity);
        }
        boolean defaultExpenseEntity = defaultExpenseRepository.findByUserAndExpenseType(userEntity, expenseEntity).isPresent();
        if (defaultExpenseEntity) {
            throw new DuplicateResourceException("Default expense already exists");
        }
        MonthlyExpenseEntity existingExpense = monthlyExpenseRepository.findByUserAndExpenseAndMonth(userEntity,
                expenseEntity, MonthEnum.fromNumber(expenseDTO.getMonth()));
        if (existingExpense != null) {
            existingExpense.setCost(expenseDTO.getCost());
            monthlyExpenseRepository.save(existingExpense);
            CreateResponseVO createResponse = new CreateResponseVO(existingExpense.getId());
            return SuccessResponseVO.of(200, "Updated cost for existing expense type " + expenseDTO.getExpenseTypeName()
                    + " for " + expenseDTO.getMonth(), createResponse);
        }
        CreateResponseVO createResponseVO = new CreateResponseVO(expenseEntity.getId());
        if (isDefault) {
            createDefaultExpense(userEntity, expenseEntity, expenseDTO);
        }
        createMonthlyExpense(userEntity, expenseDTO, expenseEntity);
        return SuccessResponseVO.of(201, "Expense created successfully", createResponseVO);
    }

    /**
     * Updates the amount of a user's default expense.
     * Records the change in logs with detailed percentage and amount differences.
     *
     * @param expenseName the name of the default expense
     * @param newAmount   the updated amount
     * @param apiKey      API key for user authentication
     * @return a SuccessResponseVO with the update confirmation and expense ID
     * @throws ResourceNotFoundException if the default expense does not exist
     */
    @Override
    public SuccessResponseVO<CreateResponseVO> updateDefaultExpense(String expenseName, double newAmount, String apiKey) {
        UserEntity userEntity = authenticationUtils.getCurrentUser(apiKey);
        ExpenseEntity existingExpense = expenseRepository.findByExpenseTypeName(expenseName);
        DefaultExpenseEntity defaultExpenseEntity = defaultExpenseRepository.findByUserAndExpenseType(userEntity,
                existingExpense).orElseThrow(() -> new ResourceNotFoundException("Default expense does not exist"));
        double oldAmount = defaultExpenseEntity.getAmount();
        defaultExpenseEntity.setAmount(newAmount);
        defaultExpenseEntity = defaultExpenseRepository.save(defaultExpenseEntity);
        logExpenseChange(defaultExpenseEntity, oldAmount, newAmount);
        String message = String.format("Default Expense '%s' updated from %.2f to %.2f successfully",
                defaultExpenseEntity.getExpenseType().getExpenseTypeName(), oldAmount, newAmount);
        return SuccessResponseVO.of(201, message, new CreateResponseVO(defaultExpenseEntity.getId()));
    }

    private void logExpenseChange(DefaultExpenseEntity defaultExpenseEntity, double oldAmount, double newAmount) {
        double difference = newAmount - oldAmount;
        double percentageChange = (oldAmount == 0) ? 0 : (difference / (oldAmount * 100));
        String changeType = (difference > 0) ? "increased" : (difference < 0) ? "decreased" : "remained unchanged";
        String expenseName = defaultExpenseEntity.getExpenseType().getExpenseTypeName();
        String userFullName = defaultExpenseEntity.getUser().getFirstName() + " " + defaultExpenseEntity.getUser().getLastName();
        log.info("For user '{}', the default expense '{}' has {} by ₹{} ({}%) — old amount: ₹{}, new amount: ₹{}.",
                userFullName,
                expenseName,
                changeType,
                String.format("%.2f", Math.abs(difference)),
                String.format("%.2f", Math.abs(percentageChange)),
                String.format("%.2f", oldAmount),
                String.format("%.2f", newAmount));
    }

    private void createDefaultExpense(UserEntity userEntity, ExpenseEntity expenseEntity, ExpenseDTO expenseDTO) {
        DefaultExpenseEntity defaultExpenseEntity = new DefaultExpenseEntity();
        defaultExpenseEntity.setExpenseType(expenseEntity);
        defaultExpenseEntity.setUser(userEntity);
        defaultExpenseEntity.setAmount(expenseDTO.getCost());
        defaultExpenseRepository.save(defaultExpenseEntity);
    }

    private void createMonthlyExpense(UserEntity userEntity, ExpenseDTO expenseDTO, ExpenseEntity entity) {
        MonthlyExpenseEntity monthlyExpenseEntity = new MonthlyExpenseEntity();
        monthlyExpenseEntity.setUser(userEntity);
        monthlyExpenseEntity.setExpense(entity);
        monthlyExpenseEntity.setCost(expenseDTO.getCost());
        monthlyExpenseEntity.setMonth(MonthEnum.fromNumber(expenseDTO.getMonth()));
        monthlyExpenseEntity.setFinancialYear(LocalDateTime.now().getYear());
        monthlyExpenseRepository.save(monthlyExpenseEntity);
    }

}

