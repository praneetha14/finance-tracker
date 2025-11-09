package com.finance.tracker.serviceImpl;

import com.finance.tracker.AbstractTest;
import com.finance.tracker.model.dto.UserDTO;
import com.finance.tracker.model.vo.CreateUserVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserServiceImplTest extends AbstractTest {

    @Autowired
    private UserService userService;

    @Test
    void createUserSuccessTest() {
        UserDTO userDTO = createUserDTO();
        SuccessResponseVO<CreateUserVO> responseVO = userService.createUser(userDTO);
        assertNotNull(responseVO);
        assertEquals(201, responseVO.getCode());
        assertEquals("User created successfully", responseVO.getMessage());
        assertNotNull(responseVO.getData().apiKey());
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
