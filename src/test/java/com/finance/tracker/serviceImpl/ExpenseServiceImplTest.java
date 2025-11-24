package com.finance.tracker.serviceImpl;

import com.finance.tracker.AbstractTest;
import com.finance.tracker.exception.DuplicateResourceException;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.exception.UserUnauthorizedException;
import com.finance.tracker.model.dto.ExpenseDTO;
import com.finance.tracker.model.dto.UserDTO;
import com.finance.tracker.model.vo.CreateResponseVO;
import com.finance.tracker.model.vo.CreateUserVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.service.ExpenseService;
import com.finance.tracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpenseServiceImplTest extends AbstractTest {

    @Autowired
    private ExpenseService expenseService;

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
                () -> {
                    expenseService.createExpense(expenseDTO, false, apiKey);
                });
        assertEquals("Invalid User", accessDeniedException.getMessage());
    }

    @Test
    void createDefaultExpenseAlreadyExistsTest() {
        ExpenseDTO expenseDTO = createExpenseDTO();
        SuccessResponseVO<CreateUserVO> createUserVO = userService.createUser(createUserDTO());
        expenseService.createExpense(expenseDTO, true, createUserVO.getData().apiKey());
        DuplicateResourceException duplicateResourceException = assertThrows(DuplicateResourceException.class,
                () -> expenseService.createExpense(expenseDTO, false, createUserVO.getData().apiKey()));
        assertEquals("Default expense already exists", duplicateResourceException.getMessage());
    }

    @Test
    void createDefaultExpenseAlreadyExistsExceptionTest() {
        ExpenseDTO expenseDTO = createExpenseDTO();
        SuccessResponseVO<CreateUserVO> createUserVO = userService.createUser(createUserDTO());
        expenseService.createExpense(expenseDTO, true, createUserVO.getData().apiKey());
        ExpenseDTO expenseDTO2 = createExpenseDTO();
        expenseDTO2.setCost(5000);
        DuplicateResourceException duplicateResourceException = assertThrows(
                DuplicateResourceException.class, () -> expenseService.createExpense(expenseDTO2, false, createUserVO.getData().apiKey())
        );
        assertEquals("Default expense already exists", duplicateResourceException.getMessage());
    }

    @Test
    void updateExistingMonthlyExpenseTest() {
        SuccessResponseVO<CreateUserVO> createUserVO = userService.createUser(createUserDTO());
        ExpenseDTO expenseDTO = createExpenseDTO();
        expenseService.createExpense(expenseDTO, false, createUserVO.getData().apiKey());
        expenseDTO.setCost(3000);
        SuccessResponseVO<CreateResponseVO> responseVO = expenseService.createExpense(expenseDTO, false, createUserVO.getData().apiKey());
        assertNotNull(responseVO);
        assertEquals(200, responseVO.getCode());
        assertEquals("Updated cost for existing expense type " + expenseDTO.getExpenseTypeName()
                + " for " + expenseDTO.getMonth(), responseVO.getMessage());
    }

    @Test
    void updateExistingDefaultExpenseTest() {
        String apiKey = userService.createUser(createUserDTO()).getData().apiKey();
        ExpenseDTO expenseDTO = createExpenseDTO();
        expenseService.createExpense(expenseDTO, true, apiKey);
        double newAmount = 4000;
        SuccessResponseVO<CreateResponseVO> responseVO = expenseService.updateDefaultExpense(expenseDTO.getExpenseTypeName(), newAmount, apiKey);
        assertNotNull(responseVO);
        assertEquals(201, responseVO.getCode());
        String expectedMessage = String.format("Default Expense '%s' updated from %.2f to %.2f successfully",
                expenseDTO.getExpenseTypeName(),
                expenseDTO.getCost(),
                newAmount);
        assertEquals(expectedMessage, responseVO.getMessage());
    }

    @Test
    void updateDefaultExpenseNotFoundTest() {
        SuccessResponseVO<CreateUserVO> createUserVO = userService.createUser(createUserDTO());
        ResourceNotFoundException resourceNotFoundException = assertThrows(
                ResourceNotFoundException.class, () -> expenseService.updateDefaultExpense("Default expense does not exist", 3000, createUserVO.getData().apiKey()));
        assertEquals("Default expense does not exist", resourceNotFoundException.getMessage());
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
