package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.ChapterDTO;
import com.lowquality.serverwebm.models.DTO.MangadetailDTO;
import com.lowquality.serverwebm.models.DTO.PagesDTO;
import com.lowquality.serverwebm.models.entity.Chapter;
import com.lowquality.serverwebm.models.entity.Mangadetail;
import com.lowquality.serverwebm.models.entity.Pages;
import com.lowquality.serverwebm.models.entity.User;
import com.lowquality.serverwebm.repository.ChapterRepository;
import com.lowquality.serverwebm.repository.PagesRepository;
import com.lowquality.serverwebm.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChapterService {
    private final PagesRepository pagesRepository;
    private final MangaService mangaService;
    private final ChapterRepository chapterRepository;
    private final PageService pageService;
    private final PermissionService permissionService;
    private final FileStorageService fileStorageService;

    public ChapterService(PagesRepository pagesRepository, ChapterRepository chapterRepository, MangaService mangaService, PageService pageService, PermissionService permissionService, FileStorageService fileStorageService) {
        this.pagesRepository = pagesRepository;
        this.chapterRepository = chapterRepository;
        this.mangaService = mangaService;
        this.pageService = pageService;
        this.permissionService = permissionService;
        this.fileStorageService = fileStorageService;
    }


    public Chapter findById(Integer id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found: " + id));
    }

    private ChapterDTO convertToDTO(Chapter chapter) {
        if (chapter == null) {
            return null;
        }

        MangadetailDTO mangadetailDTO = null;
        if (chapter.getManga() != null) {
            mangadetailDTO = mangaService.getMangaById(chapter.getManga().getId());
        }

        return ChapterDTO.builder()
                .id(chapter.getId())
                .chapter_name(chapter.getName())
                .manga_id(mangadetailDTO)
                .build();
    }

    public ChapterDTO addChapter(String chapterName,Integer chapNumber, Integer mangaId) {
        Mangadetail manga = mangaService.getMangaEntityById(mangaId);

        Chapter chapter = new Chapter();
        chapter.setName(chapterName);
        chapter.setManga(manga);
        chapter.setChapNumber(chapNumber);
        chapter = chapterRepository.save(chapter);
        return convertToDTO(chapter);
    }

    public List<ChapterDTO> getChaptersByMangaId(Integer mangaId) {
        return chapterRepository.findByManga_Id(mangaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public ChapterDTO getChapterByMangaAndNumber(Integer mangaId, Integer chapNumber) {
        Chapter chapter = chapterRepository.findByManga_IdAndChapNumber(mangaId, chapNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        return convertToDTO(chapter);
    }

    public boolean isChapterNumberExists(Integer mangaId, Integer chapNumber) {
        return chapterRepository.existsByManga_IdAndChapNumber(mangaId, chapNumber);
    }

    public void deleteChapter(Integer id){
        User user = SecurityUtils.getCurrentUser();
        Chapter chapter = findById(id);
        permissionService.checkUserPermission(chapter.getManga().getUploader().getId(),"xóa chap trong truyện này.");
        chapterRepository.delete(chapter);
    }
    public ChapterDTO addChapterWithPages(
            String chapterName,
            Integer chapterNumber,
            Integer mangaId,
            List<MultipartFile> pages) {
        User user = SecurityUtils.getCurrentUser();
        // Tạo chapter mới
        Mangadetail manga= mangaService.getMangaEntityById(mangaId);
        permissionService.checkUserPermission(manga.getUploader().getId(),"thêm chap trong truyện này.");
        Chapter chapter = new Chapter();
        chapter.setName(chapterName);
        chapter.setChapNumber(chapterNumber);
        chapter.setManga(manga); // Nếu chapter có liên kết với manga
        chapter = chapterRepository.save(chapter);

        // Thay thế phần xử lý file bằng service
        String chapterSubDir = "chapter_" + chapter.getId();
        int pageNum = 1;

        for (MultipartFile file : pages) {
            String filePath = fileStorageService.storeFile(file, chapterSubDir);

            Pages page = new Pages();
            page.setChapter(chapter);
            page.setPage_number(pageNum++);
            page.setPage_img_url(filePath);

            pageService.savePage(page);
        }

        return convertToDTO(chapter);
    }


}