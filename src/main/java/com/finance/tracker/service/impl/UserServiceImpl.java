package com.finance.tracker.service.impl;

import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.exception.InvalidInputException;
import com.finance.tracker.exception.DuplicateResourceException;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.dto.UserDTO;
import com.finance.tracker.model.vo.CreateUserVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.model.vo.UserVO;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.service.UserService;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * This class is the Implementation of the UserService interface that provides business logic
 * for managing user-related operations, including creation, retrieval, and updates.
 * This class interacts with UserRepository to perform persistence operations
 * and includes validation checks to ensure data integrity. It also handles
 * duplicate records and invalid input through custom exceptions.
 */
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /** Allowed characters for API key generation. */
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /** Secure random generator for API key creation. */
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Repository for accessing and modifying user data. */
    private final UserRepository userRepository;

    /**
     * Creates a new user in the system.
     * Performs validation of the input, checks for duplicate email or mobile, generates a unique API key,
     * and saves the new user to the database.
     */
    @Override
    public SuccessResponseVO<CreateUserVO> createUser(UserDTO userDTO) {
        validUserInput(userDTO);
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByMobile(userDTO.getMobile())) {
            throw new DuplicateResourceException("Mobile number already exists");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(userDTO.getFirstName());
        userEntity.setLastName(userDTO.getLastName());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setMobile(userDTO.getMobile());
        userEntity.setSalary(userDTO.getSalary());
        userEntity.setApiKey(generateAPIKey());
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity = userRepository.save(userEntity);
        CreateUserVO createUserVO = new CreateUserVO(userEntity.getApiKey(), userEntity.getCreatedAt(), userEntity.getCreatedBy());
        return SuccessResponseVO.of(201, "User created successfully", createUserVO);
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param apiKey authorization key for secure API access.
     * @return SuccessResponseVO containing UserVO if found
     * @throws ResourceNotFoundException if the user with the given apiKey does not exist
     */
    @Override
    public SuccessResponseVO<UserVO> getCurrentlyLoggedUserByApiKey(String apiKey) {
        UserEntity userEntity = userRepository.findByApiKey(apiKey)
                .orElseThrow(()-> new ResourceNotFoundException("User with given apiKey " + apiKey + " not found"));
        return SuccessResponseVO.of(200, "Successfully fetched user", toVO(userEntity));
    }


    /**
     * Updates an existing user's information.
     * Validates duplicate email and mobile before updating. Throws ResourceNotFoundException if the user doesn't exist.
     *
     * @param apikey authorization key for secure API access
     * @param userDTO the updated user data
     * @return SuccessResponseVO containing the updated UserVO.
     * @throws ResourceNotFoundException if no user with the given ID exists
     * @throws DuplicateResourceException if email or mobile already exists for another user
     */
    @Override
    public SuccessResponseVO<UserVO> updateUser(String apikey, UserDTO userDTO) {
        UserEntity userEntity = userRepository.findByApiKey(apikey)
                .orElseThrow(() -> new ResourceNotFoundException("User with given apiKey " + apikey + " not found"));
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists with given email address " + userDTO.getEmail());
        }
        if (userRepository.existsByMobile(userDTO.getMobile())) {
            throw new DuplicateResourceException("Mobile number already exists with given mobile " + userDTO.getMobile());
        }
        userEntity.setFirstName(userDTO.getFirstName());
        userEntity.setLastName(userDTO.getLastName());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setMobile(userDTO.getMobile());
        userEntity.setSalary(userDTO.getSalary());
        userEntity = userRepository.save(userEntity);
        return SuccessResponseVO.of(201, "User updated successfully", toVO(userEntity));
    }

    /**
     * Generates a random 20-character alphanumeric API key.
     * @return a newly generated API key string
     */
    private String generateAPIKey() {
        StringBuilder sb = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Converts a UserEntity object to a UserVO.
     * @param userEntity the user entity to convert
     * @return a corresponding UserVO instance
     */
    private UserVO toVO(UserEntity userEntity) {
        return new UserVO(userEntity.getId(), userEntity.getFirstName(), userEntity.getLastName(), userEntity.getEmail(),
                userEntity.getMobile(), userEntity.getSalary());
    }


    private void validUserInput(UserDTO userDTO) {
        if (userDTO.getFirstName() == null || userDTO.getFirstName().trim().isEmpty()) {
            throw new InvalidInputException("First name cannot be empty");
        }
        if (userDTO.getLastName() == null || userDTO.getLastName().trim().isEmpty()) {
            throw new InvalidInputException("Last name cannot be empty");
        }
        if (userDTO.getMobile() == null || !userDTO.getMobile().matches("\\d{10}")) {
            throw new InvalidInputException("Mobile number must be exactly 10 digits");
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty");
        }
        String email = userDTO.getEmail().toLowerCase();
        if (!email.endsWith("@gmail.com") && !email.endsWith("@email.com")) {
            throw new InvalidInputException("Email must end with '@gmail.com' or 'email.com'");
        }
        if (userDTO.getSalary() <= 0) {
            throw new InvalidInputException("Salary must be greater than zero");
        }
    }
}
