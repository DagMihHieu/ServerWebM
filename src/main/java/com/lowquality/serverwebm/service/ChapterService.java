package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.ChapterDTO;
import com.lowquality.serverwebm.models.DTO.MangadetailDTO;
import com.lowquality.serverwebm.models.DTO.PagesDTO;
import com.lowquality.serverwebm.models.entity.Chapter;
import com.lowquality.serverwebm.models.entity.Mangadetail;
import com.lowquality.serverwebm.models.entity.Pages;
import com.lowquality.serverwebm.repository.ChapterRepository;
import com.lowquality.serverwebm.repository.PagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ChapterService(PagesRepository pagesRepository, ChapterRepository chapterRepository, MangaService mangaService, PageService pageService) {
        this.pagesRepository = pagesRepository;
        this.chapterRepository = chapterRepository;
        this.mangaService = mangaService;
        this.pageService = pageService;
    }


    public Chapter findById(Integer id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found: " + id));
    }

    public ChapterDTO convertToDTO(Chapter chapter) {
        if (chapter == null) {
            return null;
        }

        MangadetailDTO mangadetailDTO = null;
        if (chapter.getManga() != null) {
            mangadetailDTO = mangaService.convertMangadetailToDTO(chapter.getManga());
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
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
        return convertToDTO(chapter);
    }

    public boolean isChapterNumberExists(Integer mangaId, Integer chapNumber) {
        return chapterRepository.existsByManga_IdAndChapNumber(mangaId, chapNumber);
    }
    public void deleteChapter(Integer id) {
    }

    public ChapterDTO addChapterWithPages(
            String chapterName,
            Integer chapterNumber,
            Integer mangaId,
            List<MultipartFile> pages) {

        // Tạo chapter mới
        Mangadetail manga= mangaService.getMangaEntityById(mangaId);
        Chapter chapter = new Chapter();
        chapter.setName(chapterName);
        chapter.setChapNumber(chapterNumber);
        chapter.setManga(manga); // Nếu chapter có liên kết với manga
        chapter = chapterRepository.save(chapter);

        // Tạo thư mục upload
        String uploadPath = "D:/upload/chapter_" + chapter.getId();
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        // Lưu pages
        int pageNum = 1;
        for (MultipartFile file : pages) {
            String fileName = file.getOriginalFilename();
            String filePath = uploadPath + "/" + fileName;

            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                throw new RuntimeException("Cannot save file: " + fileName, e);
            }

            // Lưu page vào DB
            Pages page = new Pages();
            page.setChapter(chapter);
            page.setPage_number(pageNum++); // Auto số page theo thứ tự upload
            page.setPage_img_url(filePath.replace("\\", "/")); // Đường dẫn file

            pageService.savePage(page);
        }

        // Trả về DTO
        return convertToDTO(chapter);
    }

}