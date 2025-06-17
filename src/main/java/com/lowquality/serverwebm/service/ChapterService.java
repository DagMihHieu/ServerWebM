package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.ChapterDTO;
import com.lowquality.serverwebm.models.entity.Chapter;
import com.lowquality.serverwebm.models.entity.Mangadetail;
import com.lowquality.serverwebm.models.entity.Pages;
import com.lowquality.serverwebm.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChapterService {

    @Autowired
    ChapterRepository chapterRepository;


    @Autowired
    PermissionService permissionService;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    PageService pageService;
    @Autowired
    @Lazy
    MangaService mangaService;

//    public ChapterService(PageService pageService, MangaService mangaService) {
//        this.pageService = pageService;
//        this.mangaService = mangaService;
//    }


    public Chapter findById(Integer id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found: " + id));
    }

    private ChapterDTO convertToDTO(Chapter chapter) {
        if (chapter == null) {
            return null;
        }
        return ChapterDTO.builder()
                .id(chapter.getId())
                .chapter_name(chapter.getName())
                .chapter_number(chapter.getChapNumber())

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
        return chapterRepository.findByManga_IdOrderByChapNumberDesc(mangaId).stream()
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
//        User user = SecurityUtils.getCurrentUser();
        Chapter chapter = findById(id);
        permissionService.checkUserPermission(chapter.getManga().getUploader().getId(),"xóa chap trong truyện này.");
//        commentRepository.deleteByChapter_Id(id);
        chapterRepository.delete(chapter);
    }
    public ChapterDTO addChapterWithPages(
            String chapterName,
            Integer chapterNumber,
            Integer mangaId,
            List<MultipartFile> pages) {
        if (isChapterNumberExists(mangaId, chapterNumber)) {
            throw new IllegalArgumentException("Chapter: "+ chapterNumber+" đã tồn tại");
        }
//        User user = SecurityUtils.getCurrentUser();
        // Tạo chapter mới
        Mangadetail manga= mangaService.getMangaEntityById(mangaId);
        permissionService.checkUserPermission(manga.getUploader().getId(),"thêm chap trong truyện này.");
        Chapter chapter = new Chapter();
        chapter.setName(chapterName);
        chapter.setChapNumber(chapterNumber);
        chapter.setManga(manga); // Nếu chapter có liên kết với manga
        chapter = chapterRepository.save(chapter);

        manga.setUpdatedAt(LocalDateTime.now());
        mangaService.save(manga);
        // Thay thế phần xử lý file bằng service
        String MangaSubDir = "Manga_" + fileStorageService.sanitizeFileName(manga.getName());
        String chapterSubDir = MangaSubDir + "/chapter_" + fileStorageService.sanitizeFileName(String.valueOf(chapter.getChapNumber()));
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