package com.lowquality.serverwebm.controller;

import java.util.List;

import com.lowquality.serverwebm.models.DTO.*;
import com.lowquality.serverwebm.models.entity.Mangadetail;
import com.lowquality.serverwebm.service.PermissionService;
import com.lowquality.serverwebm.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lowquality.serverwebm.models.entity.User;
import com.lowquality.serverwebm.service.UserService;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUserInfo(@RequestBody @Valid UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUserInfo(userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/ban/{id}")
    public ResponseEntity<UserDTO> banUser(@PathVariable int id) {
        UserDTO bannedUser = userService.banUser(id);
        return ResponseEntity.ok(bannedUser);
    }
    @PutMapping("/unban/{id}")
    public ResponseEntity<UserDTO> unBanUser(@PathVariable int id) {
        UserDTO bannedUser = userService.banUser(id);
        return ResponseEntity.ok(bannedUser);
    }
    @PostMapping("/add")
    public ResponseEntity<UserDTO> addUser(@RequestBody @Valid AddUserDTO addUserDTO) {
        UserDTO newUser = userService.addUser(addUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<UserDTO> updateAvatar(
            @PathVariable Integer userId,
            @RequestParam("avatar") MultipartFile avatar) {
        UserDTO updatedUser = userService.updateAvatar(userId, avatar);
        return ResponseEntity.ok(updatedUser);
    }
    @PutMapping("/{userId}/change-role")
    public ResponseEntity<UserDTO> changeUserRole(
            @PathVariable int userId,
            @RequestParam("roleId") int roleId) {
        UserDTO updatedUser = userService.changeUserRole(userId, roleId);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{userId}/admin-edit")
    public ResponseEntity<UserDTO> editUserByAdmin(
            @PathVariable int userId,
            @RequestBody @Valid EditUserDTO editUserDTO) {
        UserDTO updatedUser = userService.editUserByAdmin(userId, editUserDTO);
        return ResponseEntity.ok(updatedUser);
    }
} 