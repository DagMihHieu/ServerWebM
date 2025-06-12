package com.lowquality.serverwebm.controller;

import com.lowquality.serverwebm.models.DTO.StatusDTO;
import com.lowquality.serverwebm.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/api/status")
class Status {
    @Autowired
    private StatusService statusService;
    @GetMapping
    public ResponseEntity<List<StatusDTO>> getAllStatus() {
        List<StatusDTO> statusDTOList = statusService.getAllStatus();
        return ResponseEntity.ok(statusDTOList);
    }

}
