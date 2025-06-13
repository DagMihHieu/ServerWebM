package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.ApiResponse;
import com.lowquality.serverwebm.models.entity.User;
import com.lowquality.serverwebm.models.entity.VerificationCode;
import com.lowquality.serverwebm.models.entity.VerificationToken;
import com.lowquality.serverwebm.repository.UserRepository;
import com.lowquality.serverwebm.repository.VerificationCodeRepository;
import com.lowquality.serverwebm.repository.VerificationTokenRepository;
import com.lowquality.serverwebm.util.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VerificationService {
    @Autowired
    VerificationTokenRepository tokenRepository;
    @Autowired
    VerificationCodeRepository codeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private EmailService emailService;
//    public VerificationService(VerificationTokenRepository tokenRepository, UserRepository userRepository,) {
//        this.tokenRepository = tokenRepository;
//        this.userRepository = userRepository;
//    }

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
    @Transactional
    public void sendResetCode() {
        String email =SecurityUtils.getCurrentUserEmail();
        User user =userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // XÓA mã xác thực cũ nếu đã tồn tại
        codeRepository.deleteByUser(user);
        String code = String.valueOf((int)(Math.random() * 900000) + 100000); // Mã 6 chữ số

        VerificationCode resetCode = new VerificationCode();
        resetCode.setCode(code);
        resetCode.setUser(user);
        resetCode.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        codeRepository.save(resetCode);

        emailService.sendEmail(
                email,
                "Mã xác thực đổi mật khẩu",
                "Mã xác thực của bạn là: " + code
        );
    }
    @Transactional
    public boolean verifyResetCode(String email, String code) {
        email =SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));

        Optional<VerificationCode> optionalCode = codeRepository.findByUser(user);

        if (optionalCode.isEmpty()) return false;

        VerificationCode resetCode = optionalCode.get();

        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) return false;
        boolean result = resetCode.getCode().equals(code);
        consumeResetCode(user);
        return result ;
    }

    public void consumeResetCode(User user) {
        codeRepository.deleteByUser(user);
    }
}
