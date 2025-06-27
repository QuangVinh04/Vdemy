package V1Learn.spring.controller;

import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Request.LessonRequest;
import V1Learn.spring.DTO.Response.LessonResponse;
import V1Learn.spring.Service.LessonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@Slf4j
public class LessonController {
    LessonService lessonService;

    @PostMapping("/create-lesson")
    APIResponse<?> createLesson(@RequestPart("request")  LessonRequest request,
                                @RequestPart(value = "video", required = false) MultipartFile video) {
        log.info("Controller: create Lesson");
        return APIResponse.builder()
                .result(lessonService.createLesson(request, video))
                .build();
    }


    @PutMapping("/update-lesson/{lessonId}")
    APIResponse<?> updateLesson(@RequestPart("request")  LessonRequest request,
                                @RequestPart(value = "video", required = false) MultipartFile video,
                                @PathVariable("lessonId") String lessonId) {
        return APIResponse.builder()
                .result(lessonService.updateLesson(lessonId, request, video))
                .build();
    }

    @DeleteMapping("/delete-lesson/{lessonId}")
    APIResponse<String> deleteLesson(@PathVariable String lessonId) {
        lessonService.deleteLesson(lessonId);
        return APIResponse.<String>builder().result("Lesson has been deleted").build();
    }

    @GetMapping("/{lessonId}/content")
    public APIResponse<String> getLessonContent(@PathVariable String lessonId) {
        String contentUrl = lessonService.getLessonContent(lessonId);
        return APIResponse.<String>builder()
                .result(contentUrl)
                .build();
    }
}
