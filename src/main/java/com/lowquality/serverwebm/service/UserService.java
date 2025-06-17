package com.lowquality.serverwebm.service;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lowquality.serverwebm.models.DTO.*;
import com.lowquality.serverwebm.models.entity.Role;
import com.lowquality.serverwebm.models.entity.VerificationToken;
import com.lowquality.serverwebm.repository.VerificationTokenRepository;
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
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    VerificationService verificationService;
    @Autowired
    private EmailService emailService;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, PermissionService permissionService, FileStorageService fileStorageService, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.permissionService = permissionService;
        this.fileStorageService = fileStorageService;
        this.roleService = roleService;

    }

    public List<UserDTO> getAllUsers(String search) {
        permissionService.onlyModAndAdmin("Lấy thông tin tất cả người dùng");
        if (search != null && !search.isEmpty()) {
            return findUsersByNameAndEmail(search,search);
        }
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
        if (!user.get().isActive()) {
            throw new IllegalArgumentException("Tài khoản chưa được xác thực qua email");
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
//        user.setEmail(userDTO.getEmail());
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
        user.setActive(false); // chưa kích hoạt
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        // Tạo token và gửi email
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        String verifyLink = "http://localhost:8080/api/auth/verify?token=" + token;
        emailService.sendEmail(
                savedUser.getEmail(),
                "Xác nhận tài khoản",
                "Vui lòng nhấp vào liên kết để xác nhận tài khoản của bạn:\n" + verifyLink
        );

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

        // Kiểm tra không cho phép ban admin khác
        User currentUser = permissionService.getCurrentUser();
        // Không cho ban chính mình
        if (Objects.equals(user.getId(), currentUser.getId())) {
            permissionService.noPermission("Không thể ban chính mình");
        }

        // Nếu user bị ban là ADMIN
        if (permissionService.isAdmin(user)) {
            permissionService.noPermission("ban admin");
        }
        // Nếu user bị ban là MOD
        if (permissionService.isMod(user)) {
            if (!permissionService.isAdmin(currentUser)) {
                permissionService.noPermission("Chỉ admin mới được ban mod");
            }
        }
        // Nếu user bị ban là USER/UPLOADER mà currentUser không phải mod/admin
        if (!permissionService.isAdminOrMod(currentUser)) {
            permissionService.noPermission("ban người dùng");
        }
        if ( user.isActive()){
            user.setActive(false);
        }
        user.setActive(true);
        userRepository.save(user);
        return convertToDTO(user);
    }
    public UserDTO unBanUser(int id) {
        User user = findById(id);
        // Kiểm tra không cho phép ban admin khác
        User currentUser = SecurityUtils.getCurrentUser();
        if (user.getRole().getName().equals("ADMIN") || !permissionService.isAdminOrMod(currentUser)) {
            permissionService.noPermission("Bạn không thể unban admin");
        }
        if (user.getRole().getName().equals("MOD") || !permissionService.isAdmin(currentUser)) {
            permissionService.noPermission("Bạn không có quyền unban mod khác. ");
        }
        user.setActive(true);
        userRepository.save(user);
        return convertToDTO(user);
    }
    public UserDTO updateAvatar(Integer userId, MultipartFile avatar) {
        User user = findById(userId);
        boolean isOwner = user.getId().equals(user.getId());
        permissionService.checkUserPermission(user.getId(), "bạn không có quyền cập nhật avatar");
        if (!isOwner) {
            // Nếu không phải chính mình
            if (!permissionService.isAdmin(user)) {
                // Nếu không phải admin thì không được cập nhật avatar người khác
                permissionService.noPermission("Bạn không có quyền cập nhật avatar người dùng khác");
            }
            // Admin không được sửa avatar Admin khác
            if (permissionService.isAdmin(user)) {
                permissionService.noPermission("Bạn không thể cập nhật avatar của admin khác");
            }
        }
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

        String avatarUrl = UrlUtils.toPublicUrl(user.getAvatarUrl());
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
        User currentUser = permissionService.getCurrentUser();
        User userToEdit = findById(userId);
        if (!permissionService.isAdminOrMod(currentUser)) {
            permissionService.noPermission("Bạn không có quyền chỉnh sửa người dùng");
        }

        // MOD không được chỉnh sửa ADMIN hoặc MOD khác
        if (permissionService.isMod(currentUser) && (permissionService.isAdmin(userToEdit) || permissionService.isMod(userToEdit))) {
            permissionService.noPermission("Mod không thể chỉnh sửa admin hoặc mod khác");
        }

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
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        User targetUser = userRepository.findByEmail(changePasswordRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found") ) ;

        // Nếu mật khẩu cũ được cung cấp: xác thực bằng mật khẩu || admin && mod bỏ qua
            if (changePasswordRequest.getOldPassword() != null && !changePasswordRequest.getOldPassword().isBlank()) {
                permissionService.checkUserPermission(targetUser.getId(), "đổi mật khẩu");
                if (Objects.equals(passwordEncoder.encode(changePasswordRequest.getOldPassword()), targetUser.getPassword())) {
                    throw new IllegalArgumentException("Mật khẩu không chính xác");
                }
            }

        String  newPassword="";
        // Nếu mật khẩu mới không có: tạo mật khẩu ngẫu nhiên
        if (changePasswordRequest.getNewPassword() == null || changePasswordRequest.getNewPassword().isBlank()) {
          newPassword = generateRandomPassword(10);
            // Tuỳ chọn: gửi mật khẩu mới qua email
            emailService.sendEmail(targetUser.getEmail(), "Mật khẩu mới", "Mật khẩu mới của bạn là:" + newPassword);
        }
        if(changePasswordRequest.getNewPassword() != null && !changePasswordRequest.getNewPassword().isEmpty()) {
            User currUser = SecurityUtils.getCurrentUser();
            if(!permissionService.isAdminOrMod(currUser)) {
                permissionService.noPermission("Đéo có quyền");
            }
            newPassword = changePasswordRequest.getNewPassword();
        }
        targetUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(targetUser);
    }
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public List<UserDTO> findUsersByNameAndEmail(String name, String email) {
        return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(name,email ).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
