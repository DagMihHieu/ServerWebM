package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.exception.GlobalExceptionHandler;
import com.lowquality.serverwebm.models.DTO.FavoriteDTO;
import com.lowquality.serverwebm.models.entity.Favorite;
import com.lowquality.serverwebm.models.entity.Mangadetail;
import com.lowquality.serverwebm.models.entity.User;
import com.lowquality.serverwebm.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private MangaService mangaService;
    @Autowired
    private UserService userService;

    public List<FavoriteDTO> findByUserId(Integer userId) {
        return favoriteRepository.findByUser_Id(userId).stream().map(this::convertToDTO).collect(Collectors.toList());

    }
    public Favorite findById(Integer id) {
        return favoriteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));
    }
    public FavoriteDTO addFavorite(Integer userId,Integer mangaId) {
        if (favoriteRepository.findByUser_IdAndManga_Id(userId,mangaId)!=null) {
            throw new IllegalArgumentException("Manga đã nằm trong danh sách yêu thích");
        }
        User user = userService.findById(userId);
    Mangadetail manga= mangaService.getMangaEntityById(mangaId);
    Favorite favorite = new Favorite();
    favorite.setUser(user);
    favorite.setManga(manga);
    favoriteRepository.save(favorite);
    return convertToDTO(favorite);
    }
    public void removeFavorite(Integer favoriteID) {
        Favorite favorite = findById(favoriteID);
        favoriteRepository.delete(favorite);
    }

    private FavoriteDTO convertToDTO(Favorite favorite) {
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .manga(mangaService.getMangaById(favorite.getManga().getId()))
                .user_id(favorite.getUser().getId())
                .build();
    }

    public void removeFavoriteByUserAndManga(Integer userId, Integer mangaId) {
        Favorite favorite = favoriteRepository.findByUser_IdAndManga_Id(userId,mangaId);
        favoriteRepository.delete(favorite);
    }
}
