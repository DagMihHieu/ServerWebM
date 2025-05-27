package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.AuthorDTO;
import com.lowquality.serverwebm.models.DTO.CategoryDTO;
import com.lowquality.serverwebm.models.DTO.MangadetailDTO;
import com.lowquality.serverwebm.models.DTO.UserDTO;
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
import java.util.stream.Collectors;

@Service
class MangaService {
    private final MangadetailRepository mangadetailRepository;
    private final ChapterRepository chapterRepository;
    private final CategoryService categoryService;
    private final AuthorService authorService;

    @Autowired
    MangaService(MangadetailRepository mangadetailRepository, ChapterRepository chapterRepository, AuthorService authorService, CategoryService categoryService) {
        this.mangadetailRepository = mangadetailRepository;
        this.chapterRepository = chapterRepository;
        this.categoryService = categoryService;
        this.authorService = authorService;
    }
    public Mangadetail getMangaEntityById(Integer id) {
        Mangadetail manga = mangadetailRepository.findByMangaId(id);
        if (manga == null) {
            throw new IllegalArgumentException("Manga not found: " + id);
        }
        return manga;
    }
    public void deleteChapter(Chapter chapter) {
        chapterRepository.delete(chapter);
    }
    public void deleteManga(Mangadetail mangadetail) {
        mangadetailRepository.delete(mangadetail);
    }
    public void addCategories(Integer mangaId, List<Integer> categoryIds) {
        Mangadetail manga = mangadetailRepository.findByMangaId(mangaId);
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
        Mangadetail manga = mangadetailRepository.findByMangaId(mangaId);
        if (manga == null) {
            throw new IllegalArgumentException("Manga not found");
        }

        manga.getCategories().removeIf(category -> category.getId().equals(categoryId));
        mangadetailRepository.save(manga);
    }

    public void addAuthorToManga(Integer mangaId, Integer authorId) {
        Mangadetail manga = mangadetailRepository.findByMangaId(mangaId);
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
        Mangadetail manga = mangadetailRepository.findByMangaId(id);
        if (manga == null) {
            throw new IllegalArgumentException("Manga not found: " + id);
        }
        return convertMangadetailToDTO(manga);
    }

    public MangadetailDTO convertMangadetailToDTO(Mangadetail mangadetail){
        AuthorDTO authorDTO = authorService.convertToDTO(mangadetail.getAuthor_id());
        return MangadetailDTO.builder()
                .id(mangadetail.getId())
                .name(mangadetail.getName())
                .description(mangadetail.getDescription())
                .status(mangadetail.getStatus())
                .cover_img(mangadetail.getCover_img())
                .id_author(authorDTO)
                .build();
    }

}
