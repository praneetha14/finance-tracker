package com.finance.tracker.repository;

import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.model.vo.UserVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    //UserEntity getCurrentlyLoggedUserByApiKey(String apiKey);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    Optional<UserEntity> findByApiKey(String apiKey);

}
