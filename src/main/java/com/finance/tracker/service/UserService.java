package com.finance.tracker.service;

import com.finance.tracker.model.dto.UserDTO;
import com.finance.tracker.model.vo.CreateUserVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.model.vo.UserVO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    SuccessResponseVO<CreateUserVO> createUser(UserDTO userDTO);
    SuccessResponseVO<UserVO> getCurrentlyLoggedUserByApiKey(String apiKey);
    SuccessResponseVO<UserVO> updateUser(String apiKey, UserDTO userDTO);
}
