package com.finance.tracker.serviceImpl;

import com.finance.tracker.AbstractTest;
import com.finance.tracker.exception.UserUnauthorizedException;
import com.finance.tracker.model.dto.ExpenseDTO;
import com.finance.tracker.model.dto.UserDTO;
import com.finance.tracker.model.vo.CreateResponseVO;
import com.finance.tracker.model.vo.CreateUserVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.repository.MonthlyExpenseRepository;
import com.finance.tracker.service.ExpenseService;
import com.finance.tracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpenseServiceImplTest extends AbstractTest {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private MonthlyExpenseRepository monthlyExpenseRepository;

    @Autowired
    private UserService userService;

    @Test
    void createExpenseSuccessTest() {
        ExpenseDTO expenseDTO = createExpenseDTO();
        SuccessResponseVO<CreateUserVO> createUserVO = userService.createUser(createUserDTO());
        SuccessResponseVO<CreateResponseVO> responseVO = expenseService.createExpense(expenseDTO, false,
                createUserVO.getData().apiKey());
        assertNotNull(responseVO);
        assertEquals(201, responseVO.getCode());
        assertEquals("Expense created successfully", responseVO.getMessage());
        assertNotNull(responseVO.getData().getId());
    }

    @Test
    void createExpenseUserNotFoundTest() {
        ExpenseDTO expenseDTO = createExpenseDTO();
        String apiKey = "a4rgbn";
        Throwable accessDeniedException = assertThrows(UserUnauthorizedException.class,
                () -> {expenseService.createExpense(expenseDTO, false, apiKey);});
        assertEquals("Invalid User", accessDeniedException.getMessage());
    }

    private ExpenseDTO createExpenseDTO() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseTypeName("Party");
        expenseDTO.setMonth(11);
        expenseDTO.setCost(2000);
        return expenseDTO;
    }

    private UserDTO createUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@gmail.com");
        userDTO.setMobile("9811098211");
        userDTO.setSalary(100000);
        return userDTO;
    }
}
