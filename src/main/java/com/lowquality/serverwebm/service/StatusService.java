package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.StatusDTO;
import com.lowquality.serverwebm.models.entity.Status;
import com.lowquality.serverwebm.repository.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
class StatusService {
    StatusRepository statusRepository;
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }
    public void deleteStatus(Integer id) {
        statusRepository.deleteById(id);
    }
    public List<StatusDTO> getAllStatus(){
        return statusRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public StatusDTO convertToDTO(Status status){
        return StatusDTO.builder()
                .id(status.getId())
                .status_name(status.getStatus_name())
                .build();
    }
}
