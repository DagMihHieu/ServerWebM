package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.PagesDTO;
import com.lowquality.serverwebm.models.entity.Chapter;
import com.lowquality.serverwebm.models.entity.Pages;
import com.lowquality.serverwebm.repository.ChapterRepository;
import com.lowquality.serverwebm.repository.PagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PageService {
    @Autowired
    PagesRepository pagesRepository;
    @Autowired
    ChapterRepository chapterRepository;
    public PageService(PagesRepository pagesRepository) {
        this.pagesRepository = pagesRepository;
    }

    private PagesDTO convertToDTO(Pages pages){
        return PagesDTO.builder()
                .id(pages.getId())
                .page_number(pages.getPage_number())
                .page_img_url(pages.getPage_img_url())
                .build();
    }
    public List<PagesDTO> getPagesOfChapter(Integer mangaId, Integer chapterNum) {
        Chapter chapter = chapterRepository.findByManga_IdAndChapNumber(mangaId, chapterNum)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        return pagesRepository.findByChapter_id(chapter.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public void deleteByChapterId( Integer chapId ) {
      List<Pages> pages = pagesRepository.findByChapter_id(chapId);
        pagesRepository.deleteAll(pages);
    }
    public void savePage(Pages page) {
        pagesRepository.save(page);
    }
//    public List<PagesDTO> getPagesOfChapter(Integer mangaId, Integer chapterNum) {
//        Chapter chapter = null;
//        if ((mangaId != null) && (chapterNum != null)) {
//            chapter = chapterRepository.findChapterByManga_IdAndChapter_number(mangaId, chapterNum);
//        }
//        assert chapter != null;
//        return pagesRepository.findByChapter_id(chapter.getId()).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }


}
