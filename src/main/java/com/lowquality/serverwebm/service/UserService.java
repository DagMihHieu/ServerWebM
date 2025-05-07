package com.lowquality.serverwebm.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lowquality.serverwebm.models.DTO.LoginResponse;
import com.lowquality.serverwebm.models.DTO.RegisterRequest;
import com.lowquality.serverwebm.models.DTO.UserDTO;
import com.lowquality.serverwebm.models.entity.Role;
import com.lowquality.serverwebm.models.entity.User;
import com.lowquality.serverwebm.repository.RoleRepository;
import com.lowquality.serverwebm.repository.UserRepository;
import com.lowquality.serverwebm.security.JwtService;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public LoginResponse login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không chính xác");
        }
        String token = jwtService.generateToken(user.get());
        return LoginResponse.builder()
            .token(token)
            .user(convertToDTO(user.get()))
            .build();
    }

    public UserDTO register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tạitại");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setActive(true);
        
        // Get or create USER role
        Role userRole = roleRepository.findByName("USER")
            .orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName("USER");
                return roleRepository.save(newRole);
            });
        user.setRoles(Collections.singleton(userRole));

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO getUserById(int id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .avatarUrl(user.getAvatarUrl())
            .googleId(user.getGoogleId())
            .roles(user.getRoles())
            .isActive(user.isActive())
            .build();
    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
