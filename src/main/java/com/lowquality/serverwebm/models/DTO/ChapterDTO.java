package com.lowquality.serverwebm.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDTO {
    private Integer id;
    private String chapter_name;
    private MangadetailDTO manga_id;
}
