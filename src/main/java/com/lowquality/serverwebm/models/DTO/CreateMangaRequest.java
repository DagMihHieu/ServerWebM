package com.lowquality.serverwebm.models.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateMangaRequest {
    @NotBlank
    private String name;
    private String description;
    private MultipartFile coverImg;
    private String authorName;
    private Integer statusId;
    private List<Integer> categoryIds;
}