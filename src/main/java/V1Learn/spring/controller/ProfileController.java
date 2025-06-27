package V1Learn.spring.controller;


import V1Learn.spring.DTO.Request.ProfileUpdateRequest;
import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Response.ProfileResponse;
import V1Learn.spring.Service.CloudinaryService;
import V1Learn.spring.Service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@Slf4j
public class ProfileController {

    ProfileService profileService;
    CloudinaryService cloudinaryService;

    @PutMapping("/update-profile")
    APIResponse<Void> updateProfile(@RequestBody ProfileUpdateRequest request){
        profileService.updateProfile(request);

        return APIResponse.<Void>builder()
                .message("Profile updated successfully")
                .build();
    }

    @GetMapping("/info-user")
    APIResponse<ProfileResponse> getUserProfile(){
        var result = profileService.getInfoProfile();
        return APIResponse.<ProfileResponse>builder()
                .message("Profile info successfully")
                .result(result)
                .build();
    }

    @PostMapping("/me/avatar")
    APIResponse<String> uploadAvatar(@RequestParam MultipartFile file) {
        return APIResponse.<String>builder()
                .message("Profile upload avatar successfully")
                .result(profileService.uploadAvatar(file))
                .build();
    }

    @GetMapping("/me/avatar")
    APIResponse<String> getAvatar() {
        return APIResponse.<String>builder()
                .message("Get avatar successfully")
                .result(profileService.getAvatar())
                .build();
    }

}
