package V1Learn.spring.controller;

import V1Learn.spring.DTO.Request.*;
import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Response.UserResponse;
import V1Learn.spring.Service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping("/register")
    APIResponse<?> createUser(@RequestBody @Valid UserCreationRequest request)
            throws MessagingException, UnsupportedEncodingException {
        log.info("Controller: create User");
        userService.createUser(request);
        return APIResponse.builder()
                .result("Registration successful. Please check your email for the verification code.")
                .build();
    }



    @PostMapping("/confirm")
    APIResponse<UserResponse> confirmUser(@RequestBody VerifyOtpRequest request) {
        log.info("Controller: confirm User");
        return APIResponse.<UserResponse>builder()
                .result(userService.confirmUser(request))
                .build();
    }

    @PostMapping("/send-otp-forgot-password")
    APIResponse<?> sendOtpForgotPassword(@RequestBody EmailRequest request)
            throws MessagingException, UnsupportedEncodingException  {
        log.info("Controller: send otp forgot password");
        userService.sendOtpForgotPassword(request);
        return APIResponse.builder()
                .result("Send Otp Successfully.")
                .build();
    }

    @PostMapping("/verify-otp")
    APIResponse<Boolean> verifyOtp(@RequestBody VerifyOtpRequest request,
                                   @RequestParam(required = false) String key) {
        log.info("Controller: verify otp");
        return APIResponse.<Boolean>builder()
                .result(userService.verifyOtp(request, key))
                .build();
    }
    @PostMapping("/create-password")
    APIResponse<Void> createPassword(@RequestBody @Valid PasswordCreationFirstRequest request){
        userService.createPassword(request);

        return APIResponse.<Void>builder()
                .message("Password has ben created, you could use it to log-in")
                .build();
    }

    @PostMapping("/reset-password")
    APIResponse<?> resetPassword(@RequestBody ResetPasswordRequest request)  {
        log.info("Controller: reset password");
        userService.resetPassword(request);
        return APIResponse.builder()
                .result("Reset Password Successfully.")
                .build();
    }

    @PutMapping("/change-password")
    APIResponse<?> changePassword(@RequestBody ChangePasswordRequest request) {
        log.info("Controller: change password");
        userService.changePassword(request);
        return APIResponse.builder()
                .result("Change Password Successfully.")
                .build();
    }

    @GetMapping("/users")
    APIResponse<List<UserResponse>> getAllUsers(){
        log.info("Controller: getAllUsers");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return APIResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{userId}")
    APIResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        return APIResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }
    @GetMapping("/my-info")
    APIResponse<UserResponse> getMyInfo(){
        return APIResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PostMapping("/check-exists-user")
    APIResponse<Boolean> checkExistsUser(@RequestBody EmailRequest request){
        return APIResponse.<Boolean>builder()
                .result(userService.checkUserExists(request))
                .build();
    }

//    @GetMapping(path = "/teacher/students", produces = MediaType.APPLICATION_JSON_VALUE)
//    APIResponse<?> getAllStudents(
//            @PageableDefault(page = 0, size = 10, sort = "createdAT", direction = Sort.Direction.DESC)
//            Pageable pageable) {
//        log.info("Controller: get All Students");
//        return APIResponse.builder()
//                .result(userService.getAllStudentsByTeacher(pageable))
//                .build();
//    }

//    @DeleteMapping("/teacher/{userId}")
//    APIResponse<String> deleteUserByTeacher(@PathVariable String userId) {
//        userService.deleteUserByTeacher(userId);
//        return APIResponse.<String>builder().result("User has been deleted").build();
//    }


    @DeleteMapping("/{userId}")
    APIResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return APIResponse.<String>builder().result("User has been deleted").build();
    }
}

