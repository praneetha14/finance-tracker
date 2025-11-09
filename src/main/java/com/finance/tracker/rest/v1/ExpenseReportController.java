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

/**
 * ExpenseReportController class is responsible for managing financial reports and savings submissions
 * within the Finance Tracker application.
 * This controller provides endpoints for submitting user finances and
 * generating monthly expense reports in PDF format.
 */
@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
public class ExpenseReportController {

    private final ExpenseReportService expenseReportService;

    /**
     * Submits user financial details including salary, expenses, and savings data.
     * The submitted data is processed to calculate expected and actual savings,
     * which are then stored for report generation.
     *
     * @param savingsDTO the SavingsDTO object containing user financial and savings details.
     * @param apiKey     optional authorization key for secure API access.
     * @return a ResponseEntity containing a SuccessResponseVO with
     *         processed financial summary and HTTP status 201 (CREATED).
     */
    @PostMapping("/submit-finances")
    public ResponseEntity<SuccessResponseVO<FinancesVO>> submitFinances(@RequestBody SavingsDTO savingsDTO,
                                                                    @RequestHeader(value = "Authorization", required = false) String apiKey) {
        return new ResponseEntity<>(expenseReportService.submitFinances(savingsDTO, apiKey), HttpStatus.CREATED);
    }

    /**
     * Generates and retrieves a monthly expense report in PDF format.
     * This endpoint compiles user financial data, generates a formatted report using Freemarker templates,
     * and uploads it to cloud storage (e.g., AWS S3). The report URL is returned in the response.
     *
     * @param month  the numeric month for which the report should be generated.
     * @param year   the year corresponding to the report.
     * @param apiKey optional authorization key for secure API access.
     * @return a ResponseEntity containing a SuccessResponseVO with
     *         report file details and HTTP status 200 (OK).
     */
    @GetMapping("/report")
    public ResponseEntity<SuccessResponseVO<FileReportVO>> getExpenseReport(
            @RequestParam int month,
            @RequestParam int year,
            @RequestHeader(value = "Authorization", required = false) String apiKey) {
        return ResponseEntity.ok(expenseReportService.getExpenseReport(month, year, apiKey));
    }
}
