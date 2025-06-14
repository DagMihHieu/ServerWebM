package com.lowquality.serverwebm.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO {
    Integer id;
    private Integer page_number;
    private String page_img_url;
}
