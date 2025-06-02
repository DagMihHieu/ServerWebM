package com.lowquality.serverwebm.models.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateMangaRequest {
    @NotBlank
    private String name;
    private String description;
    private String coverImg;
    private Integer authorId;
    private Integer statusId;
    private List<Integer> categoryIds;
}