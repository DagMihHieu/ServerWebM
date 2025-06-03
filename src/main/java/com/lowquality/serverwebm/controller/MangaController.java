package com.lowquality.serverwebm.controller;

import com.lowquality.serverwebm.models.DTO.ChapterDTO;
import com.lowquality.serverwebm.models.DTO.CreateMangaRequest;
import com.lowquality.serverwebm.models.DTO.MangadetailDTO;
import com.lowquality.serverwebm.models.DTO.PagesDTO;
import com.lowquality.serverwebm.service.ChapterService;
import com.lowquality.serverwebm.service.MangaService;
import com.lowquality.serverwebm.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manga")
public class MangaController {

    private final MangaService mangaService;
    private final ChapterService chapterService;
    @Autowired
    PageService pageService;
    @Autowired
    public MangaController(MangaService mangaService, ChapterService chapterService) {
        this.mangaService = mangaService;
        this.chapterService = chapterService;
    }

    // Get all manga with optional filters
    @GetMapping
    public ResponseEntity<List<MangadetailDTO>> getManga(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) Integer statusId) {
            List<MangadetailDTO> mangaList;
            mangaList = mangaService.filterManga(search, categoryIds, statusId);
        return ResponseEntity.ok(mangaList);
    }

    @PostMapping("/{mangaId}/categories")
    public ResponseEntity<Void> addCategoriesToManga(
            @PathVariable Integer mangaId,
            @RequestBody List<Integer> categoryIds) {
        mangaService.addCategories(mangaId, categoryIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{mangaId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromManga(
            @PathVariable Integer mangaId,
            @PathVariable Integer categoryId) {
        mangaService.removeCategoryFromManga(mangaId, categoryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{mangaId}/author/{authorId}")
    public ResponseEntity<Void> addAuthorToManga(
            @PathVariable Integer mangaId,
            @PathVariable Integer authorId) {
        mangaService.addAuthorToManga(mangaId, authorId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<MangadetailDTO> getMangaById(@PathVariable Integer id) {
        return ResponseEntity.ok(mangaService.getMangaById(id));
    }

    @PostMapping
    public ResponseEntity<MangadetailDTO> createManga(
            @RequestBody CreateMangaRequest request) {
        return ResponseEntity.ok(mangaService.addManga(request));
    }
    @DeleteMapping("{mangaId}")
    public ResponseEntity<Void> deleteManga(
            @PathVariable Integer mangaId
    ){
        mangaService.deleteManga(mangaId);
        return ResponseEntity.noContent().build();
    }
}