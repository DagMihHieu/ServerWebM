
package com.lowquality.serverwebm.models.DTO;

import lombok.Data;

@Data
public class AddChapterRequest {
    private String chapterName;
    private Integer chapterNumber;
}