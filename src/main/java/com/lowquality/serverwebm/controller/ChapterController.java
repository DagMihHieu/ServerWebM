package com.lowquality.serverwebm.controller;

import com.lowquality.serverwebm.models.DTO.AddChapterRequest;
import com.lowquality.serverwebm.models.DTO.ChapterDTO;
import com.lowquality.serverwebm.models.DTO.PagesDTO;
import com.lowquality.serverwebm.service.ChapterService;
import com.lowquality.serverwebm.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/manga/{mangaId}/chapter")
public class ChapterController {
    @Autowired
    ChapterService chapterService;
    @Autowired
    PageService pageService;
//    Autowired Field Injection (tiêm trực tiếp vào biến ko cần tạo giống final (Constructor Injection))
//    @Autowired //
//    public ChapterController(ChapterService chapterService) {
//        this.chapterService = chapterService;
//    }
    @GetMapping
    public ResponseEntity<List<ChapterDTO>> getAllChapter(@PathVariable Integer mangaId) {
        return ResponseEntity.ok(chapterService.getChaptersByMangaId(mangaId));
    }
    @GetMapping("{chapterNum}")
    public ResponseEntity<List<PagesDTO>> getChapterPages(
            @PathVariable Integer mangaId,
            @PathVariable Integer chapterNum
    ) {
        return ResponseEntity.ok(pageService.getPagesOfChapter(mangaId,chapterNum));
    }
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ChapterDTO> addChapterToManga(
            @PathVariable Integer mangaId,
            @RequestParam String chapterName,
            @RequestParam Integer chapterNumber,
            @RequestParam("pages") List<MultipartFile> pages) {

        if (chapterService.isChapterNumberExists(mangaId, chapterNumber)) {
            throw new IllegalArgumentException("Chapter number already exists for this manga");
        }

        ChapterDTO chapterDTO = chapterService.addChapterWithPages(
                chapterName,
                chapterNumber,
                mangaId,
                pages
        );

        return ResponseEntity.ok(chapterDTO);
    }
@DeleteMapping("{chapterId}")
public ResponseEntity<Void> deleteChapter(@PathVariable Integer chapterId) {
        chapterService.deleteChapter(chapterId);
        return ResponseEntity.noContent().build();
    }
 @DeleteMapping("{chapterId}/pages")
    public ResponseEntity<Void> deletePagesOfChapter(@PathVariable Integer chapterId) {
        pageService.deleteByChapterId(chapterId);
    return ResponseEntity.noContent().build();
    }

}