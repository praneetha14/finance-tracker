package com.finance.tracker.serviceImpl;

import com.finance.tracker.AbstractTest;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.exception.DuplicateResourceException;
import com.finance.tracker.exception.InvalidInputException;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.dto.UserDTO;
import com.finance.tracker.model.vo.CreateUserVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.model.vo.UserVO;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.service.UserService;
import org.assertj.core.error.ShouldNotContainValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceImplTest extends AbstractTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUserSuccessTest() {
        UserDTO userDTO = createUserDTO();
        SuccessResponseVO<CreateUserVO> responseVO = userService.createUser(userDTO);
        assertNotNull(responseVO);
        assertEquals(201, responseVO.getCode());
        assertEquals("User created successfully", responseVO.getMessage());
        assertNotNull(responseVO.getData().apiKey());
    }

    @Test
    void createUserEmailAlreadyExistsTest(){
        UserDTO userDTO = createUserDTO();
        userService.createUser(userDTO);
        DuplicateResourceException duplicateResourceException = assertThrows(DuplicateResourceException.class,
                ()->{userService.createUser(userDTO);});
        assertEquals("Email already exists", duplicateResourceException.getMessage());
    }

    @Test
    void createUserPhoneInvalidTest(){
        UserDTO userDTO = createUserDTO();
        userDTO.setMobile("809876112");
        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class,
                ()->{userService.createUser(userDTO);});
        assertEquals("Mobile number must be exactly 10 digits", invalidInputException.getMessage());
    }

    @Test
    void createUserEmailInvalidTest(){
        UserDTO userDTO = createUserDTO();
        userDTO.setEmail("abc@i.com");
        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class,
                ()->{userService.createUser(userDTO);});
        assertEquals("Email must end with '@gmail.com' or 'email.com'", invalidInputException.getMessage());
    }

    @Test
    void createUserEmailNullFailureTest(){
        UserDTO userDTO = createUserDTO();
        userDTO.setEmail(null);
        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class,
                ()->{userService.createUser(userDTO);});
        assertEquals("Email cannot be empty", invalidInputException.getMessage());
    }

    @Test
    void createUserSalaryNullFailureTest(){
        UserDTO userDTO = createUserDTO();
        userDTO.setSalary(-100);
        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class,
                ()->{userService.createUser(userDTO);});
        assertEquals("Salary must be greater than zero", invalidInputException.getMessage());
    }

    @Test
    void createUserFirstNameNullFailureTest(){
        UserDTO userDTO = createUserDTO();
        userDTO.setFirstName(null);
        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class,
                ()->{userService.createUser(userDTO);});
        assertEquals("First name cannot be empty", invalidInputException.getMessage());
    }

    @Test
    void createUserLastNameNullFailureTest(){
        UserDTO userDTO = createUserDTO();
        userDTO.setLastName(null);
        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class,
                ()->{userService.createUser(userDTO);});
        assertEquals("Last name cannot be empty", invalidInputException.getMessage());
    }

    @Test
    void createUserPhoneAlreadyExistsTest(){
        UserDTO userDTO = createUserDTO();
        userService.createUser(userDTO);
        userDTO.setEmail("abc@gmail.com");
        DuplicateResourceException duplicateResourceException = assertThrows(DuplicateResourceException.class,
                ()->{userService.createUser(userDTO);});
        assertEquals("Mobile number already exists", duplicateResourceException.getMessage());
    }


    @Test
    void getCurrentlyLoggedUserByApikeySuccessTest() {
        SuccessResponseVO<CreateUserVO> userVO = userService.createUser(createUserDTO());
        SuccessResponseVO<UserVO> currentUserVO = userService.getCurrentlyLoggedUserByApiKey(userVO.getData().apiKey());
        assertNotNull(currentUserVO);
    }

    @Test
    void getCurrentlyLoggedUserByApikeyFailureTest() {
        String apiKey = "abc";
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
                ()->{userService.getCurrentlyLoggedUserByApiKey(apiKey);});
        assertEquals("User with given apiKey " + apiKey + " not found", resourceNotFoundException.getMessage());
    }

    @Test
    void updateUserSuccessTest() {
        SuccessResponseVO<CreateUserVO> createUserVO = userService.createUser(createUserDTO());
        UserDTO updatedDTO = createUserDTO();
        updatedDTO.setEmail("abc@i.com");
        updatedDTO.setMobile("809876112");
        updatedDTO.setSalary(200000);
        SuccessResponseVO<UserVO> responseVO = userService.updateUser(createUserVO.getData().apiKey(), updatedDTO);
        assertNotNull(responseVO);
        assertEquals(201, responseVO.getCode());
        assertEquals("User updated successfully", responseVO.getMessage());
    }

    @Test
    void updateUserNotFoundExceptionTest(){
        UserDTO updatedDTO = createUserDTO();
        String apiKey = "abc";
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
                () ->{userService.updateUser(apiKey, updatedDTO);});
        assertEquals("User with given apiKey " + apiKey + " not found", resourceNotFoundException.getMessage());
    }

    @Test
    void updateUserEmailAlreadyExistsTest(){
        UserDTO userDTO = createUserDTO();
        SuccessResponseVO<CreateUserVO> userVO = userService.createUser(userDTO);
        DuplicateResourceException duplicateResourceException = assertThrows(DuplicateResourceException.class,
                ()->{userService.updateUser(userVO.getData().apiKey(), userDTO);});
        assertEquals("Email already exists with given email address " + userDTO.getEmail(), duplicateResourceException.getMessage());
    }

    @Test
    void updateUserPhoneAlreadyExistsTest(){
        UserDTO userDTO = createUserDTO();
        userDTO.setEmail("test@gmail.com");
        SuccessResponseVO<CreateUserVO> userVO = userService.createUser(createUserDTO());
        DuplicateResourceException duplicateResourceException = assertThrows(DuplicateResourceException.class,
                ()->{userService.updateUser(userVO.getData().apiKey(), userDTO);});
        assertEquals("Mobile number already exists with given mobile " + userDTO.getMobile(), duplicateResourceException.getMessage());
    }

    private UserDTO createUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john@gmail.com");
        userDTO.setMobile("9811207654");
        userDTO.setSalary(80000.00);
        return userDTO;
    }

}
