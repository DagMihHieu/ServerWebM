package com.lowquality.serverwebm.controller;

import com.lowquality.serverwebm.models.DTO.MangadetailDTO;
import com.lowquality.serverwebm.service.MangaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manga")
public class MangaController {

    private final MangaService mangaService;

    @Autowired
    public MangaController(MangaService mangaService) {
        this.mangaService = mangaService;
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

    // Other existing endpoints...
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
}