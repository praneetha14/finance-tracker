package com.finance.tracker.rest.v1;

import com.finance.tracker.model.dto.UserDTO;
import com.finance.tracker.model.vo.CreateUserVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.model.vo.UserVO;
import com.finance.tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * UserController class handles all user-related operations in the Finance Tracker application.
 * This controller provides endpoints for creating, retrieving, updating, and listing users.
 * It delegates business logic to the UserService layer and ensures consistent API responses
 * wrapped in SuccessResponseVO object.
 * </p>
 */
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user in the system.
     * @param userDTO is the UserDTO object containing new user details.
     * @return a ResponseEntity containing SuccessResponseVO with the created user details
     * and an HTTP status of HttpStatus.
     */
    @PostMapping("/create")
    public ResponseEntity<SuccessResponseVO<CreateUserVO>> createUser(@RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }

    /**
     * Retrieves user information by user ID.
     * @param apiKey authorization key for secure API access.
     * @return a ResponseEntity containing SuccessResponseVO with user details.
     */
    @GetMapping("/get")
    public ResponseEntity<SuccessResponseVO<UserVO>> getCurrentlyLoggedUserByApiKey(@RequestHeader(value = "Authorization", required = false) String apiKey) {
        return ResponseEntity.ok(userService.getCurrentlyLoggedUserByApiKey(apiKey));
    }


    /**
     * Updates an existing user's details.
     * @param apiKey authorization key for secure API access.
     * @param userDTO is the UserDTO containing updated user details.
     * @return a ResponseEntity containing  SuccessResponseVO with updated user information.
     */
    @PutMapping("/update")
    public ResponseEntity<SuccessResponseVO<UserVO>> updateUser(@RequestHeader(value = "Authorization", required = false) String apiKey, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(apiKey, userDTO));
    }
}
