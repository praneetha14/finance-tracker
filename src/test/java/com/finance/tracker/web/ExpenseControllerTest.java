package com.finance.tracker.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.tracker.exception.InvalidInputException;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.dto.ExpenseDTO;
import com.finance.tracker.model.vo.CreateResponseVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.rest.v1.ExpenseController;
import com.finance.tracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
@ExtendWith(SpringExtension.class)
public class ExpenseControllerTest {
    private static final String BASE_URL = "/api/v1/expenses";
    private static final String CREATE_URL = BASE_URL + "/create-expense";
    private static final String UPDATE_URL = BASE_URL + "/update-default-expense";

    /***
     *     Creates a mock of UserService, so the real service code isn’t executed.
     */
    @MockBean
    private ExpenseService expenseService;

    /***
     * Converts Java objects ↔ JSON strings.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /***
     * Used to simulate HTTP calls (like POST, GET, PUT) to the controller.
     */
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createExpenseSuccessTest() throws Exception {
        ExpenseDTO expenseDTO = createExpenseDTO();
        SuccessResponseVO<CreateResponseVO> responseVO = SuccessResponseVO.of(200, "Expense created successfully", null);
        when(expenseService.createExpense(any(ExpenseDTO.class), anyBoolean(), anyString()))
                .thenReturn(responseVO);
        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseDTO))
                .param("isDefault", "false")
        ).andExpect(status().isCreated());
    }

    @Test
    void createExpenseFailureTest() throws Exception {
        when(expenseService.createExpense(any(ExpenseDTO.class), anyBoolean(), anyString()))
                .thenThrow(new InvalidInputException("Invalid Input"));
        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void updateExpenseSuccessTest() throws Exception {
        ExpenseDTO expenseDTO = createExpenseDTO();
        expenseDTO.setCost(210000);
        SuccessResponseVO<CreateResponseVO> responseVO = SuccessResponseVO.of(201, "Expense updated successfully", new CreateResponseVO(UUID.randomUUID()));
        when(expenseService.updateDefaultExpense(anyString(), anyDouble(), anyString()))
                .thenReturn(responseVO);
        mockMvc.perform(patch(UPDATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseDTO))
                .param("expenseName", "fuel")
                .param("newAmount", "2000")
        ).andExpect(status().isOk());
    }

    @Test
    void updateExpenseNotFoundFailureTest() throws Exception {
        when(expenseService.updateDefaultExpense(anyString(), anyDouble(), any()))
                .thenThrow(new ResourceNotFoundException("Default expense not found"));
        mockMvc.perform(patch(UPDATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("expenseName", "oil")
                .param("newAmount", "2500.00")
        ).andExpect(status().isNotFound());
    }

    @Test
    void updateExpenseBadRequestFailureTest() throws Exception {
        when(expenseService.updateDefaultExpense(anyString(), anyDouble(), any()))
                .thenThrow(new InvalidInputException("Invalid Input"));
        mockMvc.perform(patch(UPDATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .param("expenseName", "fuel")
                .param("newAmount", "-2500")
        ).andExpect(status().isBadRequest());
    }

    private ExpenseDTO createExpenseDTO() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseTypeName("Rent");
        expenseDTO.setCost(20000);
        expenseDTO.setMonth(11);
        return expenseDTO;
    }
}
