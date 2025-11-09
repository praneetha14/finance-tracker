package com.finance.tracker.repository;

import com.finance.tracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    Optional<UserEntity> findByApiKey(String apiKey);

    UserEntity getUserById(UUID id);
}
