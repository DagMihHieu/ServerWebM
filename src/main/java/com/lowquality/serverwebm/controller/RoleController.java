package com.lowquality.serverwebm.controller;

import com.lowquality.serverwebm.models.DTO.RoleDTO;
import com.lowquality.serverwebm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController()
@RequestMapping("/api/roles")
class RoleController {
    @Autowired
    private RoleService roleService;
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getRoles() {
        return ResponseEntity.ok(roleService.getAllRole());
    }
}
