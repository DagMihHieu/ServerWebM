package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.*;
import com.lowquality.serverwebm.models.entity.Author;
import com.lowquality.serverwebm.models.entity.Category;
import com.lowquality.serverwebm.models.entity.Chapter;
import com.lowquality.serverwebm.models.entity.Mangadetail;
import com.lowquality.serverwebm.repository.CategoriesRepository;
import com.lowquality.serverwebm.repository.ChapterRepository;
import com.lowquality.serverwebm.repository.MangadetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MangaService {
    private final MangadetailRepository mangadetailRepository;
    private final ChapterRepository chapterRepository;
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final StatusService statusService;

    @Autowired
    MangaService(MangadetailRepository mangadetailRepository, ChapterRepository chapterRepository, AuthorService authorService, CategoryService categoryService, StatusService statusService) {
        this.mangadetailRepository = mangadetailRepository;
        this.chapterRepository = chapterRepository;
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.statusService = statusService;
    }
    public Mangadetail getMangaEntityById(Integer id) {
        return mangadetailRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Manga not found: " + id));
    }
    public void deleteChapter(Chapter chapter) {
        chapterRepository.delete(chapter);
    }
    public void deleteManga(Mangadetail mangadetail) {
        mangadetailRepository.delete(mangadetail);
    }
    public void addCategories(Integer mangaId, List<Integer> categoryIds) {
        Mangadetail manga = this.getMangaEntityById(mangaId);
        if (manga == null) {
            throw new IllegalArgumentException("Manga not found");
        }

        List<Category> categories = new ArrayList<>();
        for (Integer categoryId : categoryIds) {
            Category category = categoryService.findById(categoryId);
            if (category != null) {
                categories.add(category);
            } else {
                throw new IllegalArgumentException("Category not found: " + categoryId);
            }
        }

        // Thêm các category mới
        manga.getCategories().addAll(categories);

        // Lưu lại
        mangadetailRepository.save(manga);
    }
    public void removeCategoryFromManga(Integer mangaId, Integer categoryId) {
        Mangadetail manga = this.getMangaEntityById(mangaId);
        if (manga == null) {
            throw new IllegalArgumentException("Manga not found");
        }

        manga.getCategories().removeIf(category -> category.getId().equals(categoryId));
        mangadetailRepository.save(manga);
    }

    public void addAuthorToManga(Integer mangaId, Integer authorId) {
        Mangadetail manga = this.getMangaEntityById(mangaId);
        if (manga == null) {
            throw new IllegalArgumentException("Manga not found");
        }

        Author author = authorService.findById(authorId);
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
        if (manga == null) {
            throw new IllegalArgumentException("Manga not found: " + id);
        }
        return convertMangadetailToDTO(manga);
    }

    public MangadetailDTO convertMangadetailToDTO(Mangadetail mangadetail) {
        AuthorDTO authorDTO = authorService.convertToDTO(mangadetail.getAuthor_id());

        List<CategoryDTO> categoryDTOs = mangadetail.getCategories().stream()
                .map(categoryService::convertToDTO)
                .collect(Collectors.toList());

        StatusDTO statusDTO = statusService.convertToDTO(mangadetail.getStatus_id());

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
    public List<MangadetailDTO> filterManga(String search, List<Integer> categoryIds, Integer statusId) {
        // Start with all manga
        List<Mangadetail> mangaList = mangadetailRepository.findAll();

        // Apply filters sequentially
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

        return mangaList.stream()
                .map(this::convertMangadetailToDTO)
                .collect(Collectors.toList());
    }
}
