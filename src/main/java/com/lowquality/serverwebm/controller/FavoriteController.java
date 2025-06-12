package com.lowquality.serverwebm.controller;

import com.lowquality.serverwebm.models.DTO.FavoriteDTO;
import com.lowquality.serverwebm.service.FavoriteService;
import com.lowquality.serverwebm.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin("*")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getFavoritesByUser() {
        List<FavoriteDTO> favorites = favoriteService.findByUserId();
        return ResponseEntity.ok(favorites);
    }

    // Thêm manga vào yêu thích
    @PostMapping("/{mangaId}")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Integer mangaId
    ) {
         favoriteService.addFavorite(mangaId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{mangaId}")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable Integer mangaId
    ) {
        return  ResponseEntity.ok(favoriteService.isFavorite(mangaId));
    }

    // Xóa manga khỏi yêu thích
    @DeleteMapping("/{mangaId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Integer mangaId
    ) {
        favoriteService.removeFavoriteByUserAndManga( mangaId);
        return ResponseEntity.noContent().build();
    }
//    // Xóa manga khỏi yêu thích
//    @DeleteMapping("/{favoriteId}")
//    public ResponseEntity<Void> removeFavoriteById(
//            @PathVariable Integer favoriteId
//    ) {
//        favoriteService.removeFavorite( favoriteId);
//        return ResponseEntity.noContent().build();
//    }
}
