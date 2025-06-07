package com.lowquality.serverwebm.models.DTO;

import com.lowquality.serverwebm.models.entity.Mangadetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDTO {
    private Integer id;
    private Integer user_id;
    private MangadetailDTO manga;

}
