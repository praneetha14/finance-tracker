package com.finance.tracker.rest.v1;

import com.finance.tracker.model.vo.ExpenseSummaryVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.service.MonthlyExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/monthly-expense")
@RequiredArgsConstructor
public class MonthlyExpenseController {

    private final MonthlyExpenseService monthlyExpenseService;

    @GetMapping("/summary")
    public ResponseEntity<SuccessResponseVO<ExpenseSummaryVO>> getMonthlyExpense(
            @RequestParam int month,
            @RequestParam int year,
            @RequestHeader(value = "Authorization", required = false) String apiKey){
                return ResponseEntity.ok(monthlyExpenseService.getMonthlyExpense(month, year, apiKey));
    }
}
