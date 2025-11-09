package com.finance.tracker.service.utils;

import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
public class AuthenticationUtils {

    private final UserRepository userRepository;

    public UserEntity getCurrentUser(String apiKey) throws AccessDeniedException {
        return userRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new AccessDeniedException("Invalid User"));
    }
}
