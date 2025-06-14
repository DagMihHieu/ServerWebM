package com.lowquality.serverwebm.controller;

import com.lowquality.serverwebm.models.DTO.PageDTO;
import com.lowquality.serverwebm.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manga/{mangaId}/chapter/{chapterNum}/pages")
public class PageController {
    @Autowired
    private PageService pageService;
    @GetMapping()
    public ResponseEntity<List<PageDTO>> getChapterPages(
            @PathVariable Integer mangaId,
            @PathVariable Integer chapterNum
    ) {
        return ResponseEntity.ok(pageService.getPagesOfChapter(mangaId,chapterNum));
    }
//    @DeleteMapping()
//    public ResponseEntity<Void> deletePagesOfChapter(
//
//            @PathVariable Integer chapterId
//    ) {
//        pageService.deleteByChapterId(chapterId);
//        return ResponseEntity.noContent().build();
//    }
}
