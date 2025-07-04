package com.lowquality.serverwebm.models.DTO;

import com.lowquality.serverwebm.models.entity.Author;
import com.lowquality.serverwebm.models.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MangadetailDTO {
    private Integer id;
    private String name;
    private AuthorDTO id_author;
    private String uploader;
    private String cover_img;
    private String description;
    private List<CategoryDTO> id_category;
    private StatusDTO id_status;
    private List<ChapterDTO> chapter;
}
