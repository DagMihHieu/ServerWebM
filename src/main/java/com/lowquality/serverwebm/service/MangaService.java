package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.*;
import com.lowquality.serverwebm.models.entity.*;
import com.lowquality.serverwebm.repository.ChapterRepository;
import com.lowquality.serverwebm.repository.MangadetailRepository;
import com.lowquality.serverwebm.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class MangaService {
    @Autowired
    MangadetailRepository mangadetailRepository;
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    CategoryService categoryService;
    @Autowired
    AuthorService authorService;
    @Autowired
    StatusService statusService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private UserService userService;

    //    @Autowired
//    MangaService(MangadetailRepository mangadetailRepository, ChapterRepository chapterRepository, AuthorService authorService, CategoryService categoryService, StatusService statusService) {
//        this.mangadetailRepository = mangadetailRepository;
//        this.chapterRepository = chapterRepository;
//        this.categoryService = categoryService;
//        this.authorService = authorService;
//        this.statusService = statusService;
//    }
    public Mangadetail getMangaEntityById(Integer id) {
        return mangadetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manga not found: " + id));
    }
    public void deleteManga(Integer id) {
        Mangadetail manga = this.getMangaEntityById(id);
        permissionService.checkUserPermission(manga.getUploader().getId(),"xóa truyện này");
        mangadetailRepository.delete(manga);
    }
    public void addCategories(Integer mangaId, List<Integer> categoryIds) {
        Mangadetail manga = this.getMangaEntityById(mangaId);
        permissionService.checkUserPermission(manga.getUploader().getId(),"cập nhật danh mục");
        List<Category> categories = new ArrayList<>();
        for (Integer categoryId : categoryIds) {
            Category category = categoryService.findById(categoryId);
                categories.add(category);
        }

        // Thêm các category mới
        manga.getCategories().addAll(categories);

        // Lưu lại
        mangadetailRepository.save(manga);
    }

    public void removeCategoryFromManga(Integer mangaId, Integer categoryId) {
        Mangadetail manga = this.getMangaEntityById(mangaId);
        permissionService.checkUserPermission(manga.getUploader().getId(),"Xóa danh mục");
        manga.getCategories().removeIf(category -> category.getId().equals(categoryId));
        mangadetailRepository.save(manga);
    }
// cập nhật tác giả
    public void addAuthorToManga(Integer mangaId, String authorName) {
        Mangadetail manga = this.getMangaEntityById(mangaId);
        permissionService.checkUserPermission(manga.getUploader().getId(),"cập nhật tác giả");
        Author author = new Author();
        author.setAuthor_name(authorName);
        manga.setAuthor_id(author);
        mangadetailRepository.save(manga);
    }

    public List<MangadetailDTO> getAllManga() {
        return mangadetailRepository.findAll().stream()
                .map(this::convertMangadetailToDTO)
                .collect(Collectors.toList());
    }

    public MangadetailDTO getMangaById(Integer id) {
        Mangadetail manga = this.getMangaEntityById(id);
        return convertMangadetailToDTO(manga);
    }

    private MangadetailDTO convertMangadetailToDTO(Mangadetail mangadetail) {
        AuthorDTO authorDTO = authorService.getAuthorById(mangadetail.getAuthor_id().getId());

        List<CategoryDTO> categoryDTOs = mangadetail.getCategories().stream()
                .map(categoryService::convertToDTO)
                .collect(Collectors.toList());

        StatusDTO statusDTO = statusService.convertToDTO(mangadetail.getStatus_id());
        UserDTO userDTO = userService.getUserById(mangadetail.getUploader().getId());
        return MangadetailDTO.builder()
                .id(mangadetail.getId())
                .name(mangadetail.getName())
                .description(mangadetail.getDescription())
                .cover_img(mangadetail.getCover_img())
                .id_author(authorDTO)
                .id_category(categoryDTOs)
                .id_status(statusDTO)
                .build();
    }
    //filter
    public List<MangadetailDTO> filterManga(String search, List<Integer> categoryIds, Integer statusId, Integer authorId) {
        // Start with all manga
        List<Mangadetail> mangaList = mangadetailRepository.findAll();

        // Apply filters
        if (search != null && !search.isEmpty()) {
            mangaList = mangaList.stream()
                    .filter(m -> m.getName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            mangaList = mangaList.stream()
                    .filter(m -> m.getCategories().stream()
                            .anyMatch(c -> categoryIds.contains(c.getId())))
                    .collect(Collectors.toList());
        }

        if (statusId != null) {
            mangaList = mangaList.stream()
                    .filter(m -> m.getStatus_id() != null && statusId.equals(m.getStatus_id().getId()))
                    .collect(Collectors.toList());
        }
        if (authorId != null) {
            mangaList = mangaList.stream()
                    .filter(m -> m.getStatus_id() != null && authorId.equals(m.getAuthor_id().getId()))
                    .collect(Collectors.toList());
        }

        return mangaList.stream()
                .map(this::convertMangadetailToDTO)
                .collect(Collectors.toList());
    }

public MangadetailDTO addManga(CreateMangaRequest request) {
        permissionService.checkAddMangaPermission();
        // Tạo manga mới
        Mangadetail manga = new Mangadetail();
        manga.setName(request.getName());
        manga.setDescription(request.getDescription());
        manga.setCover_img(request.getCoverImg());

        // Set author nếu có
        if (request.getAuthorId() != null) {
            Author author = authorService.findById(request.getAuthorId());
            manga.setAuthor_id(author);
        }

        // Set status nếu có
        if (request.getStatusId() != null) {
            Status status = statusService.findById(request.getStatusId());
            manga.setStatus_id(status);
        }

        // Lưu manga
        manga = mangadetailRepository.save(manga);

        // Thêm categories nếu có
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            this.addCategories(manga.getId(), request.getCategoryIds());
        }
        return convertMangadetailToDTO(manga);
    }


}
