package com.finance.tracker.service.impl;

import com.finance.tracker.entity.DefaultExpenseEntity;
import com.finance.tracker.entity.ExpenseReportEntity;
import com.finance.tracker.entity.MonthlyExpenseEntity;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.exception.DuplicateResourceException;
import com.finance.tracker.exception.InvalidInputException;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.dto.MonthlyExpenseReportModel;
import com.finance.tracker.model.dto.SavingsDTO;
import com.finance.tracker.model.enums.MonthEnum;
import com.finance.tracker.model.vo.FileReportVO;
import com.finance.tracker.model.vo.FinancesVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.repository.DefaultExpenseRepository;
import com.finance.tracker.repository.ExpenseReportRepository;
import com.finance.tracker.repository.MonthlyExpenseRepository;
import com.finance.tracker.service.CloudService;
import com.finance.tracker.service.ExpenseReportService;
import com.finance.tracker.service.PdfService;
import com.finance.tracker.service.utils.AuthenticationUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is the Implementation of  ExpenseReportService interface responsible for managing user financial reports.
 *
 * This service provides features for:
 * Recording monthly financial plans (savings and expenses).
 * Generating detailed monthly expense reports in PDF format.
 * Uploading reports to AWS S3 via CloudService and returning a pre-signed access URL.
 * The service integrates multiple components:
 * AuthenticationUtils for validating user identity using an API key.
 * ExpenseReportRepository, MonthlyExpenseRepository, and DefaultExpenseRepository for fetching and persisting expense data.
 *  PdfService for creating downloadable expense report documents.
 *  CloudService for storing and retrieving files securely in the cloud.
 *
 * Each generated report includes monthly expense analysis, comparisons with the previous month,
 * expected vs. actual spending, and savings metrics.
 */
@RequiredArgsConstructor
public class ExpenseReportServiceImpl implements ExpenseReportService {

    private final AuthenticationUtils authenticationUtils;
    private final ExpenseReportRepository expenseReportRepository;
    private final MonthlyExpenseRepository monthlyExpenseRepository;
    private final DefaultExpenseRepository defaultExpenseRepository;
    private final PdfService pdfService;
    private final CloudService cloudService;

    /**
     * Submits and records user financial expectations for a given month.
     * Validates user authentication, checks logical correctness (e.g., savings ≤ salary),
     * and stores the calculated financial summary.
     *
     * @param savingsDTO user-submitted expected savings and month information.
     * @param apiKey     unique key used to authenticate the user.
     * @return a success response containing calculated financial summary data.
     * @throws InvalidInputException if the provided savings or expenses exceed the user's salary.
     */
    @Override
    public SuccessResponseVO<FinancesVO> submitFinances(SavingsDTO savingsDTO, String apiKey) {
        UserEntity userEntity = authenticationUtils.getCurrentUser(apiKey);
        MonthEnum monthEnum = MonthEnum.fromNumber(savingsDTO.getMonth());
        double salary = userEntity.getSalary();
        double expectedSavings = savingsDTO.getExpectedSavings();
        if (expectedSavings > salary) {
            throw new InvalidInputException("Expected savings cannot be greater than salary");
        }
        double expectedExpenses = calculateExpectedExpenses(userEntity, monthEnum);
        if ((expectedExpenses + expectedSavings) > salary) {
            throw new InvalidInputException("Invalid input: Expected expenses (" + expectedExpenses +
                    ") and expected savings (" + expectedSavings + ") together exceed total salary");
        }
        ExpenseReportEntity expenseReportEntity = new ExpenseReportEntity();
        expenseReportEntity.setUser(userEntity);
        expenseReportEntity.setMonth(monthEnum);
        expenseReportEntity.setEstimatedSavings(savingsDTO.getExpectedSavings());
        expenseReportEntity.setExpectedExpenses(expectedExpenses);
        expenseReportEntity.setFinancialYear(LocalDateTime.now().getYear());
        expenseReportRepository.save(expenseReportEntity);
        double remainingAmountToUse = userEntity.getSalary() - expectedExpenses
                - expenseReportEntity.getEstimatedSavings();
        return SuccessResponseVO.of(200, "Finances recorded successfully",
                new FinancesVO(userEntity.getSalary(), expectedExpenses, savingsDTO.getExpectedSavings(),
                        remainingAmountToUse, null));
    }

    /**
     * Generates a detailed monthly expense report for the authenticated user.
     * The report includes current and previous month comparisons, expense category changes,
     * and saving trends. The final report is exported to PDF and stored in AWS S3, with
     * a pre-signed URL returned for secure access.
     *
     * @param month  the month for which the report is to be generated.
     * @param year   the corresponding year.
     * @param apiKey the user's authentication key.
     * @return a success response containing a FileReportVO with the report's pre-signed URL.
     * @throws ResourceNotFoundException if no expense data exists for the given month.
     */
    @Override
    public SuccessResponseVO<FileReportVO> getExpenseReport(int month, int year, String apiKey) {
        UserEntity userEntity = authenticationUtils.getCurrentUser(apiKey);
        if(financeReportGenerated(userEntity, month)){
            throw new DuplicateResourceException("Finance report for this month has already been generated");
        }
        Optional<ExpenseReportEntity> expenseReportEntity = expenseReportRepository.findByUserAndMonth(userEntity,
                MonthEnum.fromNumber(month));
        if (expenseReportEntity.isPresent()) {
            ExpenseReportEntity reportEntity = expenseReportEntity.get();
            return SuccessResponseVO.of(200, "Finance report generated successfully",
                    new FileReportVO(cloudService.generatePreSignedUrl(reportEntity.getFileKey())));
        }
        // Prepare monthly and comparative data
        MonthEnum currentMonthEnum = MonthEnum.fromNumber(month);
        MonthEnum previousMonthEnum = MonthEnum.fromNumber(month == 1 ? 12 : month - 1);
        int previousYear = (month == 1 ? year - 1 : year);
        List<MonthlyExpenseEntity> currentMonthlyExpenses = monthlyExpenseRepository.findByUserAndMonthAndFinancialYear(userEntity, currentMonthEnum, year);
        List<MonthlyExpenseEntity> previousMonthlyExpenses = monthlyExpenseRepository.findByUserAndMonthAndFinancialYear(userEntity, previousMonthEnum, previousYear);
        if (currentMonthlyExpenses.isEmpty()) {
            throw new ResourceNotFoundException("No monthly expenses found for this month");
        }
        List<DefaultExpenseEntity> defaultExpenses = defaultExpenseRepository.findByUser(userEntity);
        Set<String> defaultExpenseTypeNames = defaultExpenses.stream()
                .map(d -> d.getExpenseType().getExpenseTypeName())
                .collect(Collectors.toSet());
        // Calculate totals and trends
        double totalExpectedExpenses = calculateExpectedExpenses(userEntity, currentMonthEnum);
        double totalDefaultExpenses = defaultExpenses.stream()
                .mapToDouble(DefaultExpenseEntity::getAmount)
                .sum();
        double totalActualExpenses = currentMonthlyExpenses.stream()
                .mapToDouble(MonthlyExpenseEntity::getCost)
                .sum();
        double currentMonthSalary = userEntity.getSalary();
        double totalExpectedSavings = currentMonthSalary - totalExpectedExpenses;
        double totalActualSavings = currentMonthSalary - totalActualExpenses;
        double previousMonthExpenses = previousMonthlyExpenses.stream()
                .mapToDouble(MonthlyExpenseEntity::getCost).sum();
        double previousMonthSalary = expenseReportRepository.findByUserAndMonth(userEntity, previousMonthEnum)
                .map(report -> report.getUser().getSalary())
                .orElse(userEntity.getSalary());
        double previousMonthSavings = previousMonthSalary - previousMonthExpenses;
        double expensePercentageChange = calculatePercentageChange(previousMonthExpenses, totalActualExpenses);
        double savingPercentageChange = calculatePercentageChange(totalExpectedSavings, totalActualSavings);

        Map<String, Double> prevExpenseMap = previousMonthlyExpenses.stream()
                .collect(Collectors.toMap(
                        e -> e.getExpense().getExpenseTypeName(),
                        MonthlyExpenseEntity::getCost,
                        (oldValue, newValue) -> newValue
                ));
        List<MonthlyExpenseReportModel.ExpenseItem> expenseItems = currentMonthlyExpenses.stream()
                .map(exp -> {
                    String expenseName = exp.getExpense().getExpenseTypeName();
                    double prevCost = prevExpenseMap.getOrDefault(expenseName, 0.0);
                    double changeFromPrev = calculatePercentageChange(prevCost, exp.getCost());
                    boolean isDefaultExpense = defaultExpenseTypeNames.contains(expenseName);
                    return MonthlyExpenseReportModel.ExpenseItem.builder()
                            .expenseName(expenseName)
                            .expectedCost(isDefaultExpense ? exp.getCost() : 0.0)
                            .actualCost(exp.getCost())
                            .isDefault(String.valueOf(isDefaultExpense))
                            .previousMonthCost(prevCost)
                            .percentageChangeFromPreviousMonth(changeFromPrev)
                            .percentageChangeForCurrentMonth(0.0)
                            .build();
                }).collect(Collectors.toList());

        // Build detailed report model
        MonthlyExpenseReportModel reportModel = MonthlyExpenseReportModel.builder()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .mobile(userEntity.getMobile())
                .salary(currentMonthSalary)
                .month(month)
                .year(year)
                .totalExpectedExpenses(totalExpectedExpenses)
                .totalDefaultExpenses(totalDefaultExpenses)
                .actualExpenses(totalActualExpenses)
                .percentageChange(expensePercentageChange)
                .totalExpectedSavings(totalExpectedSavings)
                .actualSavings(totalActualSavings)
                .percentageChangeSavings(savingPercentageChange)
                .expenseItems(expenseItems)
                .build();
        // Calling PDF + AWS service (Generating pdf & upload to s3 bucket)
        byte[] pdfBytes = pdfService.generateMonthlyExpenseReport(reportModel);
        String fileName = "finance-reports/" + userEntity.getId() + "/" + year + "/" + MonthEnum.fromNumber(month).name() + ".pdf";
        String preSignedUrl = cloudService.uploadFileToCLoudStorage(
                pdfBytes, userEntity.getId(), MonthEnum.fromNumber(month), year, fileName);

        // Save report in DB
        ExpenseReportEntity reportEntity = new ExpenseReportEntity();
        reportEntity.setUser(userEntity);
        reportEntity.setMonth(currentMonthEnum);
        reportEntity.setFinancialYear(year);
        reportEntity.setFileKey(fileName);
        reportEntity.setTotalExpense(totalActualExpenses);
        reportEntity.setTotalSaving(totalActualSavings);
        reportEntity.setExpectedExpenses(totalExpectedExpenses);
        reportEntity.setEstimatedSavings(totalExpectedSavings);
        expenseReportRepository.save(reportEntity);
        return SuccessResponseVO.of(200, "Expense report generated successfully",
                new FileReportVO(preSignedUrl));
    }

    private boolean financeReportGenerated(UserEntity userEntity, int month) {
        Optional<ExpenseReportEntity> reportEntity = expenseReportRepository.findByUserAndMonthAndFinancialYear(userEntity, MonthEnum.fromNumber(month),
                LocalDateTime.now().getYear());
        return reportEntity.filter(expenseReportEntity -> expenseReportEntity.getFileKey() != null).isPresent();
    }

    private double calculatePercentageChange(double previous, double current) {
        if (previous == 0) {
            return 100;
        } else {
            return ((current - previous) / previous) * 100;
        }
    }

    private double calculateExpectedExpenses(UserEntity userEntity,
                                             MonthEnum monthEnum) {
        List<MonthlyExpenseEntity> monthlyExpenses = monthlyExpenseRepository.findByUserAndMonth(userEntity, monthEnum);
        return monthlyExpenses.stream().mapToDouble(MonthlyExpenseEntity::getCost).sum();

    }
}

