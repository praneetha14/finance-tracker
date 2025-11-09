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
    private static final String GET_URL = BASE_URL + "/get/";
    private static final String GET_ALL_URL = BASE_URL + "/getAllUsers";
    private static final String UPDATE_URL = BASE_URL + "/update/";

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
    void getUserByIdSuccessTest() throws Exception {
        UUID id = UUID.randomUUID();
        UserVO userVO = new UserVO(id, "John", "Doe", "john@gmail.com", "9876543210", 50000.0);
        when(userService.getUserById(any(UUID.class)))
                .thenReturn(SuccessResponseVO.of(200, "Successfully fetched user", userVO));

        mockMvc.perform(get(GET_URL + id))
                .andExpect(status().isOk());
    }

    @Test
    void getUserByIdNotFoundFailureTest() throws Exception {
        when(userService.getUserById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("User with given id" + UUID.randomUUID() + " notfound"));

        mockMvc.perform(get(GET_URL + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsersSuccessTest() throws Exception {
        List<UserVO> users = List.of(
                new UserVO(UUID.randomUUID(), "John", "Doe", "john@gmail.com", "9876543210", 50000.0),
                new UserVO(UUID.randomUUID(), "Jane", "Smith", "jane@email.com", "9123456789", 60000.0)
        );
        when(userService.getAllUsers())
                .thenReturn(SuccessResponseVO.of(200, "Successfully retrieved users", users));

        mockMvc.perform(get(GET_ALL_URL))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserSuccessTest() throws Exception {
        UUID id = UUID.randomUUID();
        UserDTO userDTO = createUserDTO();
        UserVO updatedUser = new UserVO(id, "Updated", "User", "updated@gmail.com", "9999999999", 75000.0);
        when(userService.updateUser(any(UUID.class), any(UserDTO.class)))
                .thenReturn(SuccessResponseVO.of(200, "User updated successfully", updatedUser));

        mockMvc.perform(put(UPDATE_URL + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserBadRequestFailureTest() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.updateUser(any(UUID.class), any(UserDTO.class)))
                .thenThrow(new InvalidInputException("Invalid input data"));

        mockMvc.perform(put(UPDATE_URL + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
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
