package com.finance.tracker.service.utils;

import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.exception.UserUnauthorizedException;
import com.finance.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationUtils {

    private final UserRepository userRepository;

    public UserEntity getCurrentUser(String apiKey) {
        return userRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new UserUnauthorizedException("Invalid User"));
    }
}
