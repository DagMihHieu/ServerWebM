package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.User;
import com.lowquality.serverwebm.models.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUser(User user);

    void deleteByUser(User user);
}
