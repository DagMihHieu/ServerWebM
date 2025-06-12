package com.lowquality.serverwebm.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lowquality.serverwebm.models.DTO.*;
import com.lowquality.serverwebm.models.entity.Role;
import com.lowquality.serverwebm.util.SecurityUtils;
import com.lowquality.serverwebm.util.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    @Autowired
    private UrlUtils urlUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, PermissionService permissionService, FileStorageService fileStorageService, RoleService roleService) {
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
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email và mật khẩu không được để trống");
        }

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Email không tồn tại");
        }

        User foundUser = user.get();
        if (foundUser.getPassword() == null) {
            throw new IllegalArgumentException("Tài khoản không hợp lệ");
        }

        if (!passwordEncoder.matches(password, foundUser.getPassword())) {
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
        User user = findById(id);
        // Kiểm tra không cho phép ban admin khác (nếu cần)
        User currentUser = SecurityUtils.getCurrentUser();
        if (user.getRole().getName().equals("ADMIN") || !permissionService.isAdminOrMod(currentUser)) {
            permissionService.noPermission("Bạn không thể ban admin");
        }
        if (user.getRole().getName().equals("MOD") || !permissionService.isAdmin(currentUser)) {
            permissionService.noPermission("Bạn không có quyền ban mod khác. ");
        }
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
//        String avatarUrl = user.getAvatarUrl();
//        avatarUrl =  UrlUtils.toPublicUrl(avatarUrl);

        String avatarUrl = urlUtils.toPublicUrl(user.getAvatarUrl());
        System.out.println("Convert user: " + user.getEmail());
        System.out.println("Role: " + (user.getRole() != null ? user.getRole().getName() : "null"));
        RoleDTO   roleDTO = roleService.getRoleById(user.getRole().getRole_Id());
        return UserDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .avatarUrl(avatarUrl)
            .role(roleDTO)
            .googleId(user.getGoogleId())
            .isActive(user.isActive())
            .build();
    }
    public UserDTO editUserByAdmin(int userId, EditUserDTO editUserDTO) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (!permissionService.isAdminOrMod(currentUser)) {
            permissionService.noPermission("Bạn không có quyền chỉnh sửa người dùng");
        }

        User userToEdit = findById(userId);
        if (editUserDTO.getRoleId() != null) {
            Role newRole = roleService.findByRoleId(editUserDTO.getRoleId());
            if (newRole == null) {
                throw new IllegalArgumentException("Role không tồn tại");
            }
            permissionService.checkChangeRolePermission( userToEdit, newRole.getName());
            userToEdit.setRole(newRole);
        }

        if (editUserDTO.getFullName() != null) {
            userToEdit.setFullName(editUserDTO.getFullName());
        }

        if (editUserDTO.getPassword() != null && !editUserDTO.getPassword().isEmpty()) {
            userToEdit.setPassword(passwordEncoder.encode(editUserDTO.getPassword()));
        }

        userToEdit.setActive(editUserDTO.isActive());
        userRepository.save(userToEdit);

        return convertToDTO(userToEdit);
    }
    public UserDTO addUser(AddUserDTO addUserDTO) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (!permissionService.isAdmin(currentUser)) {
             permissionService.noPermission("Chỉ admin có quyền thêm người dùng với role khác");
        }

        if (userRepository.existsByEmail(addUserDTO.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

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

    public UserDTO changeUserRole(int userId, int targetRoleId) {
        User targetUser = findById(userId);
        Role targetRole = roleService.findByRoleId(targetRoleId);
        if (targetRole == null) {
            throw new IllegalArgumentException("Role không tồn tại");
        }
        // Kiểm tra quyền dựa trên vai trò mục tiêu
        permissionService.checkChangeRolePermission(targetUser, targetRole.getName());

        // Cập nhật role
        targetUser.setRole(targetRole);
        userRepository.save(targetUser);

        return convertToDTO(targetUser);
    }
    public void changePassword(int userId, String oldPassword, String newPassword) {
        User targetUser = findById(userId);
        permissionService.checkUserPermission(targetUser.getId(),"Bạn không có quyền thay đổi mật khẩu người này");
        targetUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(targetUser);
    }
}
