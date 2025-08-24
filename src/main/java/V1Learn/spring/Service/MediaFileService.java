package V1Learn.spring.Service;



import V1Learn.spring.DTO.Request.MediaUploadRequest;
import V1Learn.spring.DTO.Response.MediaFileResponse;
import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.Entity.MediaFile;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Mapper.MediaFileMapper;
import V1Learn.spring.Repostiory.CourseRepository;
import V1Learn.spring.Repostiory.LessonRepository;
import V1Learn.spring.Repostiory.MediaFileRepository;
import V1Learn.spring.enums.MediaLinkType;
import V1Learn.spring.enums.ResourceType;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MediaFileService {

    MediaFileRepository mediaFileRepository;
    MediaFileMapper mediaFileMapper;
    CloudinaryService cloudinaryService;
    CourseRepository courseRepository;
    LessonRepository lessonRepository;



    @Transactional
    @PreAuthorize("isAuthenticated()")
    public MediaFileResponse upload(MediaUploadRequest request) {

        mediaFileRepository.findByPublicId(request.getPublicId())
                .ifPresent(m -> { throw new AppException(ErrorCode.MEDIA_ALREADY_EXISTS); });
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
        MediaFile m = mediaFileMapper.toMediaFile(request);
        m.setUploadedBy(userId);

        log.info("Upload media file by userId: {}", userId);
        return  mediaFileMapper.toMediaFileResponse(mediaFileRepository.save(m));
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public List<MediaFileResponse> uploadMany(List<MediaUploadRequest> requests) {
        return requests.stream()
                .map(this::upload)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public PageResponse<List<MediaFileResponse>> getListByOwner(String userId,
                                                                String type,
                                                                String folder,
                                                                Pageable pageable) {

        ResourceType resourceType = mapToResourceType(type);
        Page<MediaFile> p = mediaFileRepository.search(userId, resourceType, folder, pageable);

        List<MediaFileResponse> mediaFiles = p.stream()
                .map(mediaFileMapper::toMediaFileResponse)
                .toList();

        return PageResponse.<List<MediaFileResponse>>builder()
                .pageNo(p.getNumber())
                .pageSize(p.getNumber())
                .totalPage(p.getTotalPages())
                .items(mediaFiles)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public MediaFileResponse getById(String id) {
        MediaFile m = getOwnedMedia(id);
        return mediaFileMapper.toMediaFileResponse(m);
    }


    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void delete(String id) {
        MediaFile media = getOwnedMedia(id);

        List<MediaLinkType> inUseAt = getMediaUsages(media);
        if (!inUseAt.isEmpty()) {
            throw new AppException(
                    ErrorCode.MEDIA_IN_USE,
                    "Media đang được sử dụng tại: " + inUseAt
            );
        }

        boolean deleted = cloudinaryService.delete(
                media.getPublicId(),
                media.getResourceType().toString().toLowerCase()
        );

        if (deleted) {
            mediaFileRepository.delete(media);
        } else {
            throw new AppException(ErrorCode.MEDIA_NOT_FOUND);
        }
    }

    public List<MediaLinkType> getMediaUsages(MediaFile media) {
        List<MediaLinkType> usages = new ArrayList<>();
        String publicId = media.getPublicId();

        if (courseRepository.existsByThumbnailPublicId(publicId)) {
            usages.add(MediaLinkType.COURSE_THUMBNAIL);
        }

        if (courseRepository.existsByVideoPublicId(publicId)) {
            usages.add(MediaLinkType.COURSE_VIDEO);
        }

        if (lessonRepository.existsByVideoPublicId(publicId)) {
            usages.add(MediaLinkType.LESSON_VIDEO);
        }

        return usages;
    }


    private MediaFile getOwnedMedia(String id){
        MediaFile m = mediaFileRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.MEDIA_NOT_FOUND));
        String uid = SecurityUtils.getCurrentUserId().orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
        if (!Objects.equals(uid, m.getUploadedBy())) throw new AppException(ErrorCode.AUTH_FORBIDDEN);
        return m;
    }


    private ResourceType mapToResourceType(String type) {
        if (type == null) return null;
        log.info("Type: {}", type);
        return switch (type.toLowerCase()) {
            case "image" -> ResourceType.IMAGE;
            case "video" -> ResourceType.VIDEO;
            case "raw" -> ResourceType.RAW;
            case "auto" -> ResourceType.AUTO;
            default -> throw new AppException(ErrorCode.INVALID_TYPE);
        };
    }


    private void validateMediaNotInUse(MediaFile media) {
        String publicId = media.getPublicId();

        if (courseRepository.existsByThumbnailPublicId(publicId)) {
            throw new AppException(ErrorCode.MEDIA_IN_USE);
        }

        if (courseRepository.existsByVideoPublicId(publicId)) {
            throw new AppException(ErrorCode.MEDIA_IN_USE);
        }

        if (lessonRepository.existsByVideoPublicId(publicId)) {
            throw new AppException(ErrorCode.MEDIA_IN_USE);
        }
    }

    
}
