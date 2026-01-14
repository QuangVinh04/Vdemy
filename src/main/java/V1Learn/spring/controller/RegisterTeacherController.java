package V1Learn.spring.controller;

import V1Learn.spring.dto.request.RegisterTeacherRequest;
import V1Learn.spring.dto.response.APIResponse;
import V1Learn.spring.dto.response.PageResponse;
import V1Learn.spring.dto.response.RegisterTeacherResponse;
import V1Learn.spring.service.RegisterTeacherService;
import V1Learn.spring.enums.InstructorApplicationStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@Slf4j
public class RegisterTeacherController {
    RegisterTeacherService registerTeacherService;

    @PostMapping("/register-teacher")
    APIResponse<RegisterTeacherResponse> registerTeacher(@ModelAttribute RegisterTeacherRequest request) {
        log.info("RegisterTeacher request: {}", request);
        return APIResponse.<RegisterTeacherResponse>builder()
                .result(registerTeacherService.registerTeacher(request))
                .build();
    }


    @PostMapping("/approve/{applicationId}")
    APIResponse<RegisterTeacherResponse> approveTeacher(@PathVariable String applicationId)  {
        log.info("ApproveTeacher request: {}", applicationId);
        return APIResponse.<RegisterTeacherResponse>builder()
                .result(registerTeacherService.approveTeacher(applicationId))
                .build();
    }

    @PostMapping("/reject/{applicationId}")
    APIResponse<RegisterTeacherResponse> rejectTeacher(@PathVariable String applicationId)  {
        log.info("RejectTeacher request: {}", applicationId);
        return APIResponse.<RegisterTeacherResponse>builder()
                .result(registerTeacherService.rejectTeacher(applicationId))
                .build();
    }

    @GetMapping("/registration-teachers")
    APIResponse<PageResponse> getAllApplications(@PageableDefault(size = 10, sort = "createdAT", direction = Sort.Direction.DESC) Pageable pageable,
                                                 @RequestParam(required = false) InstructorApplicationStatus status){
        log.info("getAllApplications request: {}", pageable);
        return APIResponse.<PageResponse>builder()
                .result(registerTeacherService.getAllApplicationsByStatus(pageable, status))
                .build();
    }


    @DeleteMapping("/registration-teachers/{applicationId}")
    APIResponse<String> delete(@PathVariable String applicationId) {
        log.info("deleteRegistrationTeacher request: {}", applicationId);
        registerTeacherService.delete(applicationId);
        return APIResponse.<String>builder().result("Registration teacher has been deleted").build();
    }
}

