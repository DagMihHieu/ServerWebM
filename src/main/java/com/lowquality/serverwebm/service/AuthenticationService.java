//package com.lowquality.serverwebm.service;
//
//import java.util.Optional;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import com.lowquality.serverwebm.models.DTO.LoginResponse;
//import com.lowquality.serverwebm.models.entity.User;
//import com.lowquality.serverwebm.repository.UserRepository;
//import com.lowquality.serverwebm.security.JwtService;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class AuthenticationService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//
//    public LoginResponse handleOAuth2Login(String email, String name) {
//        Optional<User> existingUser = userRepository.findByEmail(email);
//
//        if (existingUser.isPresent()) {
//            User user = existingUser.get();
//            String token = jwtService.generateToken(user);
//            return new LoginResponse(token, user);
//        } else {
//            // Create new user if not exists
//            User newUser = new User();
//            newUser.setEmail(email);
//            newUser.setFullName(name);
//            newUser.setPassword(passwordEncoder.encode(generateRandomPassword()));
//            newUser.setActive(true);
//
//            User savedUser = userRepository.save(newUser);
//            String token = jwtService.generateToken(savedUser);
//            return new LoginResponse(token, savedUser);
//        }
//    }
//
//    private String generateRandomPassword() {
//        // Generate a random password for OAuth users
//        return java.util.UUID.randomUUID().toString();
//    }
//}