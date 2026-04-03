package V1Learn.spring.controller;

import V1Learn.spring.dto.request.HeartbeatRequest;
import V1Learn.spring.dto.response.APIResponse;

import V1Learn.spring.dto.response.ProgressUpdateResponse;
import V1Learn.spring.service.LessonProgressService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/progress")
@Slf4j
public class LessonProgressController {
    LessonProgressService lessonProgressService;

    // Cập nhật vị trí đang xem
    @PatchMapping("/{enrollmentId}/lessons/{lessonId}/heartbeat")
    public APIResponse<Void> updateHeartbeat(
            @PathVariable String enrollmentId,
            @PathVariable String lessonId,
            @RequestBody @Valid HeartbeatRequest request) {

        lessonProgressService.updateWatchProgress(enrollmentId, lessonId, request.getLastWatchedSecond());
        return APIResponse.<Void>builder()
                .message("Updated heartbeat")
                .build();
    }

    // Đánh dấu hoàn thành bài học
    @PostMapping("/{enrollmentId}/lessons/{lessonId}/complete")
    public APIResponse<ProgressUpdateResponse> markComplete(
            @PathVariable String enrollmentId,
            @PathVariable String lessonId) {

        return APIResponse.<ProgressUpdateResponse>builder()
                .message("Mark complete")
                .result(lessonProgressService.markLessonAsComplete(enrollmentId, lessonId))
                .build();
    }

}
