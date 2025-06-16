package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.*;
import com.lowquality.serverwebm.models.entity.*;
import com.lowquality.serverwebm.repository.ChapterRepository;
import com.lowquality.serverwebm.repository.MangadetailRepository;
import com.lowquality.serverwebm.util.SecurityUtils;
import com.lowquality.serverwebm.util.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.util.Set;
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
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private UrlUtils urlUtils;

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
        manga.getCategories().clear(); // Xoá toàn bộ quan hệ hiện tại
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
        author.setAuthorName(authorName);
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
        StatusDTO statusDTO = null;
        if (mangadetail.getStatus_id() != null) {
            statusDTO = statusService.getById(mangadetail.getStatus_id().getId());
        }
        AuthorDTO authorDTO = null;
        if (mangadetail.getAuthor_id() != null) {
            authorDTO = authorService.getAuthorById(mangadetail.getAuthor_id().getId());
        }
        List<CategoryDTO> categoryDTOs = mangadetail.getCategories().stream()
                .map(categoryService::convertToDTO)
                .collect(Collectors.toList());
        String coverimgURL = UrlUtils.toPublicUrl(mangadetail.getCover_img());
        return MangadetailDTO.builder()
                .id(mangadetail.getId())
                .name(mangadetail.getName())
                .description(mangadetail.getDescription())
                .cover_img(coverimgURL)
                .id_author(authorDTO)
                .id_category(categoryDTOs)
                .uploader(mangadetail.getUploader().getFullName())
                .id_status(statusDTO)
                .build();
    }
//    //filter
//    public List<MangadetailDTO> filterManga(String search, List<Integer> categoryIds, Integer statusId, Integer authorId, String sortBy, Integer uploader) {
//        List<Mangadetail> mangaList = new ArrayList<>();
//        if (uploader == null) {
//             mangaList = mangadetailRepository.findAll();
//        }else{
//            mangaList = mangadetailRepository.findByUploaderId(uploader);
//        }
//
//        // Apply filters
//        if (search != null && !search.isEmpty()) {
//            mangaList = mangaList.stream()
//                    .filter(m -> m.getName().toLowerCase().contains(search.toLowerCase()))
//                    .collect(Collectors.toList());
//        }
//
//        if (categoryIds != null && !categoryIds.isEmpty()) {
//            mangaList = mangaList.stream()
////                    .filter(m -> m.getCategories().stream()
////                            .allMatch(c -> categoryIds.contains(c.getId())))
////                    .collect(Collectors.toList());
//                    .filter(manga -> {
//                // Lấy ra danh sách ID category của manga hiện tại
//                Set<Integer> mangaCategoryIds = manga.getCategories().stream()
//                        .map(Category::getId)
//                        .collect(Collectors.toSet());
//                // Kiểm tra xem nó có chứa TẤT CẢ các ID được yêu cầu không
//                return mangaCategoryIds.containsAll(categoryIds);
//            })
//                    .collect(Collectors.toList());
//        }
//
//        if (statusId != null) {
//            mangaList = mangaList.stream()
//                    .filter(m -> m.getStatus_id() != null && statusId.equals(m.getStatus_id().getId()))
//                    .collect(Collectors.toList());
//        }
//        if (authorId != null) {
//            mangaList = mangaList.stream()
//                    .filter(m -> m.getStatus_id() != null && authorId.equals(m.getAuthor_id().getId()))
//                    .collect(Collectors.toList());
//        }
//
//        mangaList = sortManga(mangaList, sortBy);
//        System.out.println("Sorted by latest:");
//        mangaList.forEach(m -> System.out.println(m.getName() + " - " + m.getUpdatedAt()));
//        return mangaList.stream()
//                .map(this::convertMangadetailToDTO)
//                .collect(Collectors.toList());
//    }
//    private List<Mangadetail> sortManga(List<Mangadetail> mangaList, String sortBy) {
//        if (sortBy == null) return mangaList;
//
//        return switch (sortBy) {
//            case "latest" -> mangaList.stream()
//                    .sorted(Comparator.comparing(Mangadetail::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
//                    .collect(Collectors.toList());
//            case "popular" -> mangaList.stream() //vì chưa có lượt xem nên dùng chapter để so.
//                    .sorted(Comparator.comparing((Mangadetail m) -> m.getChapters().size()).reversed())
//                    .collect(Collectors.toList());
//            case "name" -> mangaList.stream()
//                    .sorted(Comparator.comparing(Mangadetail::getName, String.CASE_INSENSITIVE_ORDER))
//                    .collect(Collectors.toList());
//            case "rating" -> mangaList; // Chưa có trường rating
//            case "oldest" -> mangaList.stream()
//                    .sorted(Comparator.comparing(Mangadetail::getCreatedAt, Comparator.reverseOrder()))
//                    .collect(Collectors.toList());
//            default -> mangaList;
//
//        };
//    }
public Page<MangadetailDTO> filterManga(String search, List<Integer> categoryIds, Integer statusId, Integer authorId,
                                        String sortBy, Integer uploader, int page, int size) {

    // Tạo Pageable với thứ tự mặc định theo updatedAt nếu sortBy là latest
    Pageable pageable = switch (sortBy) {
        case "latest" -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        case "name"   -> PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        case "popular" -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "view") ); // chưa có view.
        default       -> PageRequest.of(page, size);  // fallback: không sort hoặc sort custom bằng Java
    };

    Page<Mangadetail> mangaPage = mangadetailRepository.filterMangaJPQL(
            search,
            categoryIds != null && !categoryIds.isEmpty() ? categoryIds : null,
            statusId,
            authorId,
            uploader,
            pageable
    );

    // Trường hợp cần sort lại trong Java
    List<Mangadetail> sortedList = switch (sortBy) {
        case "popular" -> mangaPage.getContent().stream()
                .sorted(Comparator.comparing((Mangadetail m) -> m.getChapters().size()).reversed())
                .toList();
        default -> mangaPage.getContent();
    };

    List<MangadetailDTO> dtoList = sortedList.stream()
            .map(this::convertMangadetailToDTO)
            .toList();

    return new PageImpl<>(dtoList, pageable, mangaPage.getTotalElements());
}

public MangadetailDTO addManga(CreateMangaRequest request) {

        User user = SecurityUtils.getCurrentUser();
        // Tạo manga mới
        permissionService.checkMangaPermission("thêm truyện");
        String MangaSubDir = "Manga_" + fileStorageService.sanitizeFileName(request.getName());
        String coverImgUrl =  fileStorageService.storeFile( request.getCoverImg(),MangaSubDir);
        Mangadetail manga = new Mangadetail();
        manga.setName(request.getName());
        manga.setDescription(request.getDescription());
        manga.setCover_img(coverImgUrl);
        manga.setUploader(user);
        // Set author nếu có
        if (request.getAuthorName() != null) {
            Author author = authorService.findByAuthor_name(request.getAuthorName());
            if (author != null) {
                manga.setAuthor_id(author);
            }
            else{
                AuthorDTO authorDTO=  authorService.createAuthor(request.getAuthorName());
                author = authorService.findById(authorDTO.getId());
                manga.setAuthor_id(author);
            }

        }


        if (request.getStatusId() != null) {
            Status status = statusService.findById(request.getStatusId());
           if (status != null) {
               manga.setStatus_id(status);
           }
        }
        Status defaultStatus = statusService.findById(1);
        manga.setStatus_id(defaultStatus);
        // Lưu manga
        manga = mangadetailRepository.save(manga);

        // Thêm categories nếu có
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            this.addCategories(manga.getId(), request.getCategoryIds());
        }
        return convertMangadetailToDTO(manga);
    }
    public void editMangadetail(Integer id,CreateMangaRequest mangaRequest) {
            //CreateMangaRequest tận dụng lại để cập nhật thông tin chapter
            permissionService.checkMangaPermission("cập nhật truyện");
            Mangadetail manga = getMangaEntityById(id);
             manga.setName(mangaRequest.getName());
             manga.setDescription(mangaRequest.getDescription());
             if(mangaRequest.getAuthorName() != null) {
                 Author author = authorService.findByAuthor_name(mangaRequest.getAuthorName());
                 manga.setAuthor_id(author);
             }
             if (mangaRequest.getStatusId() != null) {
                 Status status = statusService.findById(mangaRequest.getStatusId());
                 manga.setStatus_id(status);
             }
             if (mangaRequest.getCategoryIds() != null && !mangaRequest.getCategoryIds().isEmpty()) {
                this.addCategories(manga.getId(), mangaRequest.getCategoryIds());
            }
             if(mangaRequest.getCoverImg() != null && !mangaRequest.getCoverImg().isEmpty()) {
                 fileStorageService.deleteFile(manga.getCover_img());
                 String MangaSubDir = "Manga_" + fileStorageService.sanitizeFileName(mangaRequest.getName());
                 String coverImgUrl =  fileStorageService.storeFile( mangaRequest.getCoverImg(),MangaSubDir);
                 manga.setCover_img(coverImgUrl);
             }

                  mangadetailRepository.save(manga);
//            return convertMangadetailToDTO(manga);
    }

    public void save(Mangadetail manga) {
        mangadetailRepository.save(manga);
    }
}
