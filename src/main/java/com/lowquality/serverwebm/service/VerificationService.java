package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.ApiResponse;
import com.lowquality.serverwebm.models.entity.User;
import com.lowquality.serverwebm.models.entity.VerificationToken;
import com.lowquality.serverwebm.repository.UserRepository;
import com.lowquality.serverwebm.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VerificationService {
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public VerificationService(VerificationTokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public ApiResponse<String> verifyToken(String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            return ApiResponse.failure("Token không hợp lệ hoặc không tồn tại");
        }

        VerificationToken verificationToken = optionalToken.get();
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ApiResponse.failure("Token đã hết hạn");
        }

        User user = verificationToken.getUser();
        user.setActive(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);

        return ApiResponse.success(null, "Tài khoản đã được xác thực thành công");
    }
}
