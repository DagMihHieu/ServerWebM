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
    @Autowired
    private PermissionService permissionService;

    public List<FavoriteDTO> findByUserId() {
        User user = permissionService.getCurrentUser();
        return favoriteRepository.findByUser_Id(user.getId()).stream().map(this::convertToDTO).collect(Collectors.toList());

    }
    public Favorite findById(Integer id) {
        return favoriteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));
    }
    public FavoriteDTO addFavorite(Integer mangaId) {
        User user= permissionService.getCurrentUser();
        if (favoriteRepository.findByUser_IdAndManga_Id(user.getId(),mangaId)!=null) {
            throw new IllegalArgumentException("Manga đã nằm trong danh sách yêu thích");
        }
    Mangadetail manga= mangaService.getMangaEntityById(mangaId);
    Favorite favorite = new Favorite();
    favorite.setUser(user);
    favorite.setManga(manga);
    favoriteRepository.save(favorite);
    return convertToDTO(favorite);
    }
    public boolean isFavorite(Integer mangaId) {
        User user= permissionService.getCurrentUser();
        favoriteRepository.findByUser_IdAndManga_Id(user.getId(),mangaId);
        return favoriteRepository.findByUser_IdAndManga_Id(user.getId(), mangaId) != null;
    }
    public void removeFavorite(Integer favoriteID) {
        Favorite favorite = findById(favoriteID);
        User user = userService.findById(favorite.getUser().getId());
        permissionService.checkUserPermission(user.getId(),"thay đổi favorite");
        favoriteRepository.delete(favorite);
    }

    private FavoriteDTO convertToDTO(Favorite favorite) {
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .manga(mangaService.getMangaById(favorite.getManga().getId()))
                .user_id(favorite.getUser().getId())
                .build();
    }

    public void removeFavoriteByUserAndManga( Integer mangaId) {
        User user= permissionService.getCurrentUser();
        Favorite favorite = favoriteRepository.findByUser_IdAndManga_Id(user.getId(), mangaId);

        permissionService.checkUserPermission(user.getId(),"thay đổi favorite");
        favoriteRepository.delete(favorite);
    }
}
