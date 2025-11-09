package com.finance.tracker.rest.v1;

import com.finance.tracker.model.dto.SavingsDTO;
import com.finance.tracker.model.vo.FileReportVO;
import com.finance.tracker.model.vo.FinancesVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.service.ExpenseReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
public class ExpenseReportController {

    private final ExpenseReportService expenseReportService;

    @PostMapping("/submit-finances")
    public ResponseEntity<SuccessResponseVO<FinancesVO>> submitFinances(@RequestBody SavingsDTO savingsDTO,
                                                                    @RequestHeader(value = "Authorization", required = false) String apiKey) {
        return new ResponseEntity<>(expenseReportService.submitFinances(savingsDTO, apiKey), HttpStatus.CREATED);
    }

    @GetMapping("/report")
    public ResponseEntity<SuccessResponseVO<FileReportVO>> getExpenseReport(
            @RequestParam int month,
            @RequestParam int year,
            @RequestHeader(value = "Authorization", required = false) String apiKey) {
        return ResponseEntity.ok(expenseReportService.getExpenseReport(month, year, apiKey));
    }
}
