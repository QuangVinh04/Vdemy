

package V1Learn.spring.controller;

import V1Learn.spring.DTO.Request.ChapterRequest;
import V1Learn.spring.DTO.Request.CreateSignatureRequest;
import V1Learn.spring.DTO.Request.MediaUploadRequest;
import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Response.CreateSignatureResponse;
import V1Learn.spring.DTO.Response.MediaFileResponse;
import V1Learn.spring.Service.CloudinaryService;
import V1Learn.spring.Service.MediaFileService;
import V1Learn.spring.utils.ResourceType;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/media")
@Slf4j
public class MediaFileController {

    CloudinaryService cloudinaryService;
    MediaFileService mediaFileService;

    // Tạo chữ ký upload
    @PostMapping("/signature")
    public APIResponse<CreateSignatureResponse> signature(@Valid @RequestBody CreateSignatureRequest request) {

        return APIResponse.<CreateSignatureResponse>builder()
                .result(cloudinaryService.createSignature(request))
                .message("Create Signature successful")
                .build();
    }

    // lưu file upload
    @PostMapping("/save")
    public APIResponse<MediaFileResponse> save(@Valid @RequestBody MediaUploadRequest request) {

        return APIResponse.<MediaFileResponse>builder()
                .result(mediaFileService.upload(request))
                .message("Upload file successful")
                .build();
    }
    // luu nhieu file
    @PostMapping("/save-many")
    public APIResponse<List<MediaFileResponse>> saveMany(@Valid @RequestBody List<MediaUploadRequest> request) {

        return APIResponse.<List<MediaFileResponse>>builder()
                .result(mediaFileService.uploadMany(request))
                .message("Upload file successful")
                .build();
    }

    @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> getAllMediaByOwner(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String folder,
            @AuthenticationPrincipal(expression = "subject") String userId) {
        log.info("Get all media by owner");
        return APIResponse.builder()
                .result(mediaFileService.getListByOwner(userId, type, folder, pageable))
                .message("Get all media by owner successful")
                .build();
    }

    @GetMapping(path = "/{id}")
    public APIResponse<?> getMediaById(@PathVariable("id") String id){
        log.info("Get media by id: {}", id);
        return APIResponse.builder()
                .result(mediaFileService.getById(id))
                .message("Get media by id successful")
                .build();
    }

    @DeleteMapping("/{id}")
    public APIResponse<?> delete(@PathVariable String id) {
        log.info("delete media by id: {}", id);
        mediaFileService.delete(id);
        return APIResponse.builder()
                .result("Course has been deleted")
                .build();
    }


}