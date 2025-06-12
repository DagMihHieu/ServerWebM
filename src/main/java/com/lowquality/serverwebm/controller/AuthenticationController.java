package com.lowquality.serverwebm.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.lowquality.serverwebm.models.DTO.*;
import com.lowquality.serverwebm.models.entity.VerificationToken;
import com.lowquality.serverwebm.service.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lowquality.serverwebm.service.UserService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final UserService userService;
    private final VerificationService verificationService;

    public AuthenticationController(UserService userService, VerificationService verificationService) {
        this.userService = userService;
        this.verificationService = verificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyAccount(@RequestParam String token) {
        ApiResponse<String> response = verificationService.verifyToken(token);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(Authentication authentication) {
//        if (authentication != null) {
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Đăng xuất thành công");
//            return ResponseEntity.ok(response);
//        }
//        return ResponseEntity.badRequest().body("No active session");
//    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.checkEmailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(
//            @RequestParam(required = false) String email
    ) {
        verificationService.sendResetCode();
        return ResponseEntity.ok(ApiResponse.success(null, "Mã xác thực đã được gửi đến email của bạn"));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean valid = verificationService.verifyResetCode(email, code);
        if (valid) {
            return ResponseEntity.ok("Mã hợp lệ");
        }
        return ResponseEntity.badRequest().body("Mã không hợp lệ hoặc đã hết hạn");
    }
//    @PostMapping("/change-password")
//    public ResponseEntity<Boolean> change
} 