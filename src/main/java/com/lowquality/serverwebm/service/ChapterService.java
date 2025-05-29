package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.ChapterDTO;
import com.lowquality.serverwebm.models.DTO.MangadetailDTO;
import com.lowquality.serverwebm.models.entity.Chapter;
import com.lowquality.serverwebm.models.entity.Mangadetail;
import com.lowquality.serverwebm.repository.ChapterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChapterService {
    private final MangaService mangaService;
    private final ChapterRepository chapterRepository;

    public ChapterService(ChapterRepository chapterRepository, MangaService mangaService) {
        this.chapterRepository = chapterRepository;
        this.mangaService = mangaService;
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
        chapter.setChap_number(chapNumber);
        chapter = chapterRepository.save(chapter);
        return convertToDTO(chapter);
    }

    public List<ChapterDTO> getChaptersByMangaId(Integer mangaId) {
        return chapterRepository.findByManga_id(mangaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteChapter(Integer id) {
    }
}