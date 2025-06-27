package V1Learn.spring.Service;


import V1Learn.spring.DTO.Request.ProfileUpdateRequest;
import V1Learn.spring.DTO.Response.ProfileResponse;
import V1Learn.spring.Entity.User;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Mapper.ProfileMapper;
import V1Learn.spring.Repostiory.UserRepository;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    CloudinaryService cloudinaryService;

    @Transactional
    @PreAuthorize("isAuthenticated()")
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

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public String uploadAvatar(MultipartFile file) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String avatarUrl = null;
        // kiểm tra avatar cũ của User có tồn tại ko
        if(user.getAvatar() != null){
            cloudinaryService.deleteFile(cloudinaryService.extractPublicIdFromUrl(user.getAvatar()), "image");
            user.setAvatar(null);
        }
        if(file != null){
            avatarUrl = cloudinaryService.uploadUserAvatar(file);
            user.setAvatar(avatarUrl);
            userRepository.save(user);
        }
        return avatarUrl;
    }

    @PreAuthorize("isAuthenticated()")
    public String getAvatar() {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return user.getAvatar();
    }

}

