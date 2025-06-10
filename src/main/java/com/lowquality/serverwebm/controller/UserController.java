package com.lowquality.serverwebm.controller;

import java.util.List;

import com.lowquality.serverwebm.models.entity.Mangadetail;
import com.lowquality.serverwebm.service.PermissionService;
import com.lowquality.serverwebm.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lowquality.serverwebm.models.DTO.LoginRequest;
import com.lowquality.serverwebm.models.DTO.RegisterRequest;
import com.lowquality.serverwebm.models.DTO.UserDTO;
import com.lowquality.serverwebm.models.entity.User;
import com.lowquality.serverwebm.service.UserService;

import jakarta.validation.Valid;

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


} 