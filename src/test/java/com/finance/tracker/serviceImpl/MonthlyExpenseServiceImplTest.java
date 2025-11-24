package com.finance.tracker.serviceImpl;

import com.finance.tracker.AbstractTest;
import com.finance.tracker.model.dto.ExpenseDTO;
import com.finance.tracker.model.dto.UserDTO;
import com.finance.tracker.model.vo.ExpenseSummaryVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.service.ExpenseService;
import com.finance.tracker.service.MonthlyExpenseService;
import com.finance.tracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonthlyExpenseServiceImplTest extends AbstractTest {

    @Autowired
    private MonthlyExpenseService monthlyExpenseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseService expenseService;

    @Test
    void getMonthlyExpenseSuccessTest(){
        String apiKey = userService.createUser(createUserDTO()).getData().apiKey();
        ExpenseDTO rent = createExpenseDTO("Rent", 10000);
        ExpenseDTO electricity = createExpenseDTO("Electricity", 2000);
        expenseService.createExpense(rent, true, apiKey);
        expenseService.createExpense(electricity, true, apiKey);
        ExpenseDTO groceries = createExpenseDTO("Groceries", 3000);
        expenseService.createExpense(groceries, false, apiKey);
        SuccessResponseVO<ExpenseSummaryVO> summaryVO = monthlyExpenseService.getMonthlyExpense(11, 2025, apiKey);
        assertNotNull(summaryVO);
        assertEquals(200, summaryVO.getCode());
        assertEquals("Monthly expenses summary retrieved successfully", summaryVO.getMessage());
        ExpenseSummaryVO summary = summaryVO.getData();
        assertNotNull(summary);
        assertEquals(2, summary.getDefaultExpenses().size());
        assertTrue(summary.getDefaultExpenses()
                .stream()
                .map(ExpenseDTO :: getExpenseTypeName)
                .collect(Collectors.toSet())
                .containsAll(java.util.Set.of("Rent", "Electricity")));
        assertEquals(3, summary.getMonthlyExpenses().size());
        assertTrue(summary.getMonthlyExpenses()
                .stream()
                .anyMatch(e -> e.getExpenseTypeName().equals("Groceries")));
        double expectedTotalDefaultExpenses = 12000;
        double totalMonthlyExpectedExpenses = 3000;
        assertEquals(expectedTotalDefaultExpenses, summary.getTotalDefaultExpenses(), 0.01);
        assertEquals(totalMonthlyExpectedExpenses,
                summary.getTotalExpenses() - summary.getTotalDefaultExpenses(), 0.01);
        assertEquals(expectedTotalDefaultExpenses + totalMonthlyExpectedExpenses, summary.getTotalExpenses(), 0.01);
    }

    private ExpenseDTO createExpenseDTO(String name, double cost) {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseTypeName(name);
        expenseDTO.setMonth(11);
        expenseDTO.setCost(cost);
        return expenseDTO;
    }

    private UserDTO createUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Roy");
        userDTO.setEmail("johnry@gmail.com");
        userDTO.setMobile("9811077211");
        userDTO.setSalary(110000);
        return userDTO;
    }
}
