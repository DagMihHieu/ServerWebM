package com.lowquality.serverwebm.dto;

import com.lowquality.serverwebm.models.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private User user;
} 