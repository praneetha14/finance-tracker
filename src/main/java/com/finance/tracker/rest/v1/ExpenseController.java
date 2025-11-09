package com.finance.tracker.rest.v1;


import com.finance.tracker.model.dto.ExpenseDTO;
import com.finance.tracker.model.vo.CreateResponseVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ExpenseController class manages user and default expense operations within the Finance Tracker application.
 * This controller exposes endpoints for creating new expenses (user-specific or default)
 * and updating default expense values. It delegates business logic to the ExpenseService.
 */
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Creates a new expense record.
     * This endpoint can create either a user-specific expense or a default expense based on
     * the isDefault flag. It accepts an optional API key for authentication.
     *
     * @param expenseDTO the ExpenseDTO object containing expense details.
     * @param isDefault  flag indicating whether the expense is a default expense.
     * @param apiKey     optional authorization key for secure API access.
     * @return a ResponseEntity containing a SuccessResponseVO with
     * creation details and HTTP status 201 (CREATED).
     */
    @PostMapping("/create-expense")
    public ResponseEntity<SuccessResponseVO<CreateResponseVO>> createExpense(@RequestBody ExpenseDTO expenseDTO,
                                                                             @RequestParam boolean isDefault,
                                                                             @RequestHeader(value = "Authorization", required = false) String apiKey) {

        return new ResponseEntity<>(expenseService.createExpense(expenseDTO, isDefault, apiKey), HttpStatus.CREATED);
    }

    /**
     * Updates the amount of a default expense.
     * This endpoint allows modifying the amount of an existing default expense identified by name.
     * It requires the new amount value and supports optional API key-based authorization.
     *
     * @param expenseName the name of the default expense to update.
     * @param newAmount   the new amount to assign to the expense.
     * @param apiKey      optional authorization key for secure API access.
     * @return a ResponseEntity containing a SuccessResponseVO with update details
     * and HTTP status 200 (OK).
     */
    @PatchMapping("/update-default-expense")
    public ResponseEntity<SuccessResponseVO<CreateResponseVO>> updateDefaultExpense(
            @RequestParam String expenseName,
            @RequestParam double newAmount,
            @RequestHeader(value = "Authorization", required = false) String apiKey){
        return ResponseEntity.ok(expenseService.updateDefaultExpense(expenseName, newAmount, apiKey));
    }

}
