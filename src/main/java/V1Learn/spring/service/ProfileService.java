package V1Learn.spring.service;
// sửa

import V1Learn.spring.dto.request.ProfileUpdateRequest;
import V1Learn.spring.dto.response.ProfileResponse;
import V1Learn.spring.entity.User;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.ProfileMapper;
import V1Learn.spring.repository.UserRepository;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileService {
    UserRepository userRepository;
    ProfileMapper profileMapper;


    @Transactional
    @PreAuthorize("isAuthenticated()")
    @CacheEvict(value = "course_instructor", key = "#request.id")
    public void updateProfile(ProfileUpdateRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        profileMapper.updateUserProfile(user, request);

        userRepository.save(user);
        log.info("User profile updated successfully for user with email: {}", user.getEmail());
    }

    @PreAuthorize("isAuthenticated()")
    public ProfileResponse getInfoProfile() {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        return profileMapper.toProfileResponse(userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    @PreAuthorize("isAuthenticated()")
    public String getAvatar() {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return user.getAvatarUrl();
    }

}
