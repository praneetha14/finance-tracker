package com.finance.tracker.web;

import com.finance.tracker.exception.InvalidInputException;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.dto.ExpenseDTO;
import com.finance.tracker.model.vo.ExpenseSummaryVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.rest.v1.MonthlyExpenseController;
import com.finance.tracker.service.MonthlyExpenseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MonthlyExpenseController.class)
@ExtendWith(SpringExtension.class)
public class MonthlyExpenseControllerTest {
    private static final String BASE_URL = "/api/v1/monthly-expense";
    private static final String GET_URL = BASE_URL + "/summary";

    @MockBean
    private MonthlyExpenseService monthlyExpenseService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMonthlyExpenseSuccess() throws Exception {
        ExpenseSummaryVO expenseSummaryVO = createExpenseSummaryVO();
        SuccessResponseVO<ExpenseSummaryVO> responseVO = SuccessResponseVO.of(200, "Monthly expenses summary retrieved successfully", expenseSummaryVO);
        when(monthlyExpenseService.getMonthlyExpense(anyInt(), anyInt(), any()))
                .thenReturn(responseVO);
        mockMvc.perform(get(GET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("month", "11")
                .param("year", "2025")
        ).andExpect(status().isOk());
    }

    @Test
    void getMonthlyExpenseInvalidInputFailureTest() throws Exception {
        when(monthlyExpenseService.getMonthlyExpense(anyInt(), anyInt(), any()))
                .thenThrow(new InvalidInputException("Invalid month or year"));
        mockMvc.perform(get(GET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("month", "13")
                .param("year", "2029")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getMonthlyExpenseResourceNotFoundFailureTest() throws Exception {
        when(monthlyExpenseService.getMonthlyExpense(anyInt(), anyInt(), any()))
                .thenThrow(new ResourceNotFoundException("No monthly expense found for given month and year"));
        mockMvc.perform(get(GET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("month", "11")
                .param("year", "2025")
        ).andExpect(status().isNotFound());
    }

    private ExpenseSummaryVO createExpenseSummaryVO() {
        ExpenseSummaryVO expenseSummaryVO = new ExpenseSummaryVO(createExpenseDTOList(),createMonthlyExpense(), 3000, 9000, 20000);
        expenseSummaryVO.setDefaultExpenses(createExpenseDTOList());
        expenseSummaryVO.setMonthlyExpenses(createMonthlyExpense());
        expenseSummaryVO.setOtherExpenses(3000);
        expenseSummaryVO.setTotalExpenses(9000);
        expenseSummaryVO.setTotalDefaultExpenses(20000);
        return expenseSummaryVO;
    }

    private List<ExpenseDTO> createMonthlyExpense() {
        List<ExpenseDTO> expenseDTOList = new ArrayList<>();
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseTypeName("Travelling");
        expenseDTO.setMonth(11);
        expenseDTO.setCost(4000);
        expenseDTOList.add(expenseDTO);
        return expenseDTOList;
    }

    private List<ExpenseDTO> createExpenseDTOList() {
        List<ExpenseDTO> expenseDTOList = new ArrayList<>();
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseTypeName("Food");
        expenseDTO.setMonth(11);
        expenseDTO.setCost(2000);
        expenseDTOList.add(expenseDTO);
        return expenseDTOList;
    }
}
