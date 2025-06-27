package V1Learn.spring.Service;


import V1Learn.spring.DTO.Request.CourseUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseDraftService {

    RedisService redisService;
    ObjectMapper objectMapper;

    static final String PREVIEW_KEY = "preview:course:";
    static final String DRAFT_PREFIX = "draft:";

    /**
     * Tạo hoặc cập nhật bản preview
     * @param id có thể là draftId (cho khóa học mới) hoặc courseId (cho khóa học cũ)
     * @param request dữ liệu khóa học
     * @return previewId (draftId hoặc courseId)
     */
    public String savePreview(String id, CourseUpdateRequest request) {
        String previewId = id == null ? generateDraftId() : id; // nếu null thì tạo draftId

        try {
            String json = objectMapper.writeValueAsString(request);
            redisService.setWithTTL(
                    PREVIEW_KEY + previewId,
                    json,
                    24,
                    TimeUnit.HOURS
            );
            log.info("Saved preview for ID: {}", previewId);
            return previewId;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize preview data", e);
            throw new RuntimeException("Error while saving preview");
        }
    }

    /**
     * LưuLấy dữ liệu preview
     */
    public CourseUpdateRequest getPreview(String previewId) {
        Object value = redisService.getValue(PREVIEW_KEY + previewId);

        if (value == null) {
            throw new RuntimeException("No preview data found");
        }

        try {
            return objectMapper.readValue(value.toString(), CourseUpdateRequest.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize preview data", e);
            throw new RuntimeException("Invalid preview data format");
        }
    }

    /**
     * Xóa bản preview
     */
    public void deletePreview(String previewId) {
        redisService.deleteValue(PREVIEW_KEY + previewId);
        log.info("Deleted preview for ID: {}", previewId);
    }

    private String generateDraftId() {
        return DRAFT_PREFIX + UUID.randomUUID().toString();
    }

    public boolean isDraftId(String id) {
        return id.startsWith(DRAFT_PREFIX);
    }
}


