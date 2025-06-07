package com.lowquality.serverwebm.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Integer id;
    private String comment;
    private Integer mangaId;
    private Integer chapId;
    private boolean isDeleted;
}
