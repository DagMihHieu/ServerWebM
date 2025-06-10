package com.lowquality.serverwebm.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lowquality.serverwebm.models.DTO.AddUserDTO;
import com.lowquality.serverwebm.models.entity.Role;
import com.lowquality.serverwebm.util.SecurityUtils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lowquality.serverwebm.models.DTO.LoginResponse;
import com.lowquality.serverwebm.models.DTO.RegisterRequest;
import com.lowquality.serverwebm.models.DTO.UserDTO;
import com.lowquality.serverwebm.models.entity.User;
import com.lowquality.serverwebm.repository.UserRepository;
import com.lowquality.serverwebm.security.JwtService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PermissionService permissionService;
    private final FileStorageService fileStorageService;
    private final RoleService roleService;
    ;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, PermissionService permissionService, UserDetailsService userDetailsService, FileStorageService fileStorageService, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.permissionService = permissionService;
        this.fileStorageService = fileStorageService;
        this.roleService = roleService;
    }

    public List<UserDTO> getAllUsers() {
        permissionService.onlyModAndAdmin("Lấy thông tin tất cả người dùng");
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public LoginResponse login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() ) {
            throw new IllegalArgumentException("Email không tồn tại");
           
        }
        if ( !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new IllegalArgumentException("Mật khẩu không chính xác");
        }
        String token = jwtService.generateToken(user.get());
        return LoginResponse.builder()
            .token(token)
            .user(convertToDTO(user.get()))
            .build();
    }
    public UserDTO updateUserInfo(UserDTO userDTO) {
        User user = findById(userDTO.getId());
        permissionService.checkUserPermission(user.getId(),"bạn không có quyền cập nhật thông tin");
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        userRepository.save(user);
        return(convertToDTO(user));
    }
//    public void updateAvatar(Integer userId, MultipartFile avatar) {
//        User user = findById(userId);
//        permissionService.checkUserPermission(user.getId(),"bạn không có quyền cập nhật thông tin avatar");
//    }
    public UserDTO register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        Role role = Role.USER;
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setActive(true);
        user.setRole(role);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }
    public User findById(Integer id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    public UserDTO getUserById(int id) {
        User user= findById(id);
        permissionService.checkUserPermission(user.getId(),"lấy thông tin của người dùng này");
        return convertToDTO(user);
    }
    public UserDTO banUser(int id) {
        permissionService.onlyModAndAdmin("Bạn không có quyền ban người dùng này");
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
        return convertToDTO(user);
    }
    public UserDTO updateAvatar(Integer userId, MultipartFile avatar) {
        User user = findById(userId);
        permissionService.checkUserPermission(user.getId(), "bạn không có quyền cập nhật avatar");

        if (user.getAvatarUrl() != null) {
            fileStorageService.deleteFile(user.getAvatarUrl());
        }

        String subDirectory = "avatars/user_" + userId;
        String filePath = fileStorageService.storeFile(avatar, subDirectory);

        user.setAvatarUrl(filePath);
        userRepository.save(user);

        return convertToDTO(user);
    }
    private   UserDTO convertToDTO(User user) {
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null) {
            // Thay thế đường dẫn vật lý bằng URL public
            avatarUrl = avatarUrl.replace("D:/upload", "/upload");
        }
        return UserDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .avatarUrl(user.getAvatarUrl())
            .googleId(user.getGoogleId())
            .isActive(user.isActive())
            .build();
    }
    public UserDTO addUser(AddUserDTO addUserDTO) {
        User newUser = new User();
        newUser.setFullName(addUserDTO.getUsername());
        newUser.setEmail(addUserDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(addUserDTO.getPassword()));
        newUser.setActive(true);
        Role role = roleService.findByRoleId(addUserDTO.getRoleId());
        newUser.setRole(role);
        userRepository.save(newUser);

        return convertToDTO(newUser);

    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
