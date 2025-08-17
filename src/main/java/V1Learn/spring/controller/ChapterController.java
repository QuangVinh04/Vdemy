package V1Learn.spring.controller;

import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Request.ChapterRequest;
import V1Learn.spring.DTO.Response.ChapterResponse;
import V1Learn.spring.Service.ChapterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@Slf4j
public class ChapterController {
    ChapterService chapterService;

    @PostMapping("/create-chapter/{courseId}")
    APIResponse<?> createChapter(@PathVariable String courseId,
                                 @RequestBody ChapterRequest request) {
        log.info("Creating chapter for courseId={}", courseId);
        return APIResponse.builder()
                .result(chapterService.createChapter(courseId, request))
                .build();
    }


    @PutMapping("/update-chapter/{chapterId}")
    APIResponse<?> updateChapter(@RequestBody ChapterRequest request,
                              @PathVariable("chapterId") String chapterId) {
        log.info("Updating chapterId={}", chapterId);
        chapterService.updateChapter(chapterId, request);
        return APIResponse.builder()
                .result("Updated chapter successfully")
                .build();
    }

    @DeleteMapping("/delete-chapter/{chapterId}")
    APIResponse<String> deleteChapter(@PathVariable String chapterId) {
        log.info("Deleting chapterId={}", chapterId);
        chapterService.deleteChapter(chapterId);
        return APIResponse.<String>builder()
                .result("Chapter has been deleted")
                .build();
    }


    @GetMapping("/courses/{id}/chapters")
    public APIResponse<List<ChapterResponse>> getChapters(@PathVariable String id) {
        log.info("Request get chapter by courseId ={}", id);
        return APIResponse.<List<ChapterResponse>>builder()
                .result(chapterService.getChaptersByCourseId(id))
                .message("Get chapters by courseId successfully")
                .build();
    }




}