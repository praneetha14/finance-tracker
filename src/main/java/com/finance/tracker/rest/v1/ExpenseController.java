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

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;


    @PostMapping("/create-expense")
    public ResponseEntity<SuccessResponseVO<CreateResponseVO>> createExpense(@RequestBody ExpenseDTO expenseDTO,
                                                                             @RequestParam boolean isDefault,
                                                                             @RequestHeader(value = "Authorization", required = false) String apiKey) {

        return new ResponseEntity<>(expenseService.createExpense(expenseDTO, isDefault, apiKey), HttpStatus.CREATED);
    }

    @PatchMapping("/update-default-expense")
    public ResponseEntity<SuccessResponseVO<CreateResponseVO>> updateDefaultExpense(
            @RequestParam String expenseName,
            @RequestParam double newAmount,
            @RequestHeader(value = "Authorization", required = false) String apiKey){
        return ResponseEntity.ok(expenseService.updateDefaultExpense(expenseName, newAmount, apiKey));
    }

}
