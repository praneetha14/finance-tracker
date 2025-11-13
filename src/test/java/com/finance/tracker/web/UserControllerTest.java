package com.finance.tracker.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.tracker.BaseControllerTest;
import com.finance.tracker.exception.InvalidInputException;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.dto.UserDTO;
import com.finance.tracker.model.vo.CreateUserVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.model.vo.UserVO;
import com.finance.tracker.rest.v1.UserController;
import com.finance.tracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ExtendWith(SpringExtension.class)
public class UserControllerTest extends BaseControllerTest {
    private static final String BASE_URL = "/api/v1/users";
    private static final String CREATE_URL = BASE_URL + "/create";
    private static final String GET_URL = BASE_URL + "/get";
    private static final String UPDATE_URL = BASE_URL + "/update";

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createUserSuccessTest() throws Exception {
        UserDTO userDTO = createUserDTO();
        SuccessResponseVO<CreateUserVO> responseVO = SuccessResponseVO.of(200, "User created successfully",
                new CreateUserVO("apiKey123", null, "System"));
        when(userService.createUser(any(UserDTO.class))).thenReturn(responseVO);
        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO))
        ).andExpect(status().isCreated());
    }

    @Test
    void createUserBadRequestFailureTest() throws Exception {
        when(userService.createUser(any(UserDTO.class)))
                .thenThrow(new InvalidInputException("Invalid input"));
        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getCurrentlyLoggedUserByApiKeySuccessTest() throws Exception {
        UserVO userVO = new UserVO(UUID.randomUUID(),
                "John", "Doe", "john@email.com", "9087651109", 140000);
        when(userService.getCurrentlyLoggedUserByApiKey(anyString()))
                .thenReturn(SuccessResponseVO.of(200, "Successfully fetched user", userVO));
        mockMvc.perform(get(GET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", "apiKey123")
        ).andExpect(status().isOk());
    }

    @Test
    void getCurrentlyLoggedUserByApiKeyNotFoundFailureTest() throws Exception {
        when(userService.getCurrentlyLoggedUserByApiKey(anyString()))
                .thenThrow(new ResourceNotFoundException("User not found with the given apiKey"));
        mockMvc.perform(get(GET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", "hapiKey123")
        ).andExpect(status().isNotFound());
    }

    @Test
    void updateUserSuccessTest() throws Exception {
        UserDTO userDTO = createUserDTO();
        UserVO updatedUserVO = new UserVO(UUID.randomUUID(), "Lila", "Singh",
                "leelas@gmail.com", "9877661189", 140000);
        when(userService.updateUser(anyString(), any(UserDTO.class)))
                .thenReturn(SuccessResponseVO.of(201, "User updated successfully", updatedUserVO));
        mockMvc.perform(put(UPDATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO))
                .header("authorization", "apiKey563")
        ).andExpect(status().isOk());
    }

    @Test
    void updateUserInvalidInputFailureTest() throws Exception {
        when(userService.updateUser(anyString(), any(UserDTO.class)))
                .thenThrow(new InvalidInputException("Invalid input data"));
        mockMvc.perform(put(UPDATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .header("authorization", "apiKey563")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void updateUserNotFoundFailureTest() throws Exception {
        when(userService.updateUser(anyString(), any(UserDTO.class)))
                .thenThrow(new ResourceNotFoundException("User not found with the given apiKey"));
        mockMvc.perform(put(UPDATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .header("authorization", "h2apiKey")
        ).andExpect(status().isNotFound());
    }

    private UserDTO createUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("johndoe@gmail.com");
        userDTO.setMobile("9823411209");
        userDTO.setSalary(70000.00);
        return userDTO;
    }
}
