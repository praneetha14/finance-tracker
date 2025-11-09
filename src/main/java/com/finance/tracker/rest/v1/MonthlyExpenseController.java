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

/**
 * MonthlyExpenseController responsible for retrieving monthly expense summaries
 * for authenticated users in the Finance Tracker application.
 * This controller provides an endpoint to fetch a detailed breakdown of
 * default and additional expenses for a given month and year,
 * along with total and categorized expense summaries.
 * */
@RestController
@RequestMapping("/api/v1/monthly-expense")
@RequiredArgsConstructor
public class MonthlyExpenseController {

    private final MonthlyExpenseService monthlyExpenseService;

    /**
     * Retrieves a detailed summary of the user's monthly expenses.
     * The response includes categorized expense details (default and non-default),
     * total expenses, and breakdown by type for the specified month and year.
     *
     * @param month  the numeric value of the month for which to retrieve expenses (1–12).
     * @param year   the year corresponding to the requested month.
     * @param apiKey optional authorization key for secure API access.
     * @return a ResponseEntity containing a SuccessResponseVO with
     *         an ExpenseSummaryVO detailing the monthly expense summary.
     */
    @GetMapping("/summary")
    public ResponseEntity<SuccessResponseVO<ExpenseSummaryVO>> getMonthlyExpense(
            @RequestParam int month,
            @RequestParam int year,
            @RequestHeader(value = "Authorization", required = false) String apiKey){
                return ResponseEntity.ok(monthlyExpenseService.getMonthlyExpense(month, year, apiKey));
    }
}
