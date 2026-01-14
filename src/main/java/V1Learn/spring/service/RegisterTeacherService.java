package V1Learn.spring.service;


import V1Learn.spring.dto.request.RegisterTeacherRequest;
import V1Learn.spring.dto.response.*;
import V1Learn.spring.dto.event.NotificationEvent;
import V1Learn.spring.entity.*;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.RegisterTeacherMapper;
import V1Learn.spring.repository.*;
import V1Learn.spring.constant.PredefinedRole;
import V1Learn.spring.enums.InstructorApplicationStatus;
import V1Learn.spring.enums.NotificationType;
import V1Learn.spring.utils.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegisterTeacherService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    InstructorApplicationRepository applicationRepository;
    RegisterTeacherMapper registerTeacherMapper;
    CloudinaryService cloudinaryService;
    KafkaTemplate<String, Object> kafkaTemplate;


    @PreAuthorize("hasAuthority('ADMIN')")
    public PageResponse getAllApplicationsByStatus(Pageable pageable, InstructorApplicationStatus status) {
        if(status != null){
            Page<RegisterTeacher> applications = applicationRepository.findByOptionalStatus(status, pageable);
            List<RegisterTeacherResponse> responses = applications.stream()
                    .map(application -> RegisterTeacherResponse.builder()
                            .id(application.getId())
                            .name(application.getUser().getFullName())
                            .phone(application.getUser().getPhone())
                            .email(application.getUser().getEmail())
                            .expertise(application.getExpertise())
                            .description(application.getDescription())
                            .status(application.getStatus())
                            .certificateUrl(application.getCertificateUrl())
                            .portfolioUrl(application.getPortfolioUrl())
                            .createdAT(application.getCreatedAt())
                            .build())
                    .toList();
            return PageResponse.builder()
                    .pageNo(applications.getNumber())
                    .pageSize(applications.getSize())
                    .totalPage(applications.getTotalPages())
                    .items(responses)
                    .build();
        }
        Page<RegisterTeacher> applications = applicationRepository.findAll(pageable);
        List<RegisterTeacherResponse> responses = applications.stream()
                .map(application -> RegisterTeacherResponse.builder()
                        .id(application.getId())
                        .name(application.getUser().getFullName())
                        .phone(application.getUser().getPhone())
                        .email(application.getUser().getEmail())
                        .expertise(application.getExpertise())
                        .description(application.getDescription())
                        .status(application.getStatus())
                        .certificateUrl(application.getCertificateUrl())
                        .portfolioUrl(application.getPortfolioUrl())
                        .createdAT(application.getCreatedAt())
                        .build())
                .toList();
        return PageResponse.builder()
                .pageNo(applications.getNumber())
                .pageSize(applications.getSize())
                .totalPage(applications.getTotalPages())
                .items(responses)
                .build();
    }



    @Transactional
    @PreAuthorize("hasAuthority('USER')")
    public RegisterTeacherResponse registerTeacher(RegisterTeacherRequest request) {

        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("hello"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

//        String certificateUrl = cloudinaryService.upload(request.getCertificate(), "certificates");
//        String portfolioUrl = cloudinaryService.upload(request.getPortfolio(), "portfolios");

        RegisterTeacher registerTeacher = RegisterTeacher.builder()
                .expertise(request.getExpertise())
                .user(user)
//                .certificateUrl(certificateUrl)
//                .portfolioUrl(portfolioUrl)
                .status(InstructorApplicationStatus.PENDING)
                .build();
        applicationRepository.save(registerTeacher);
        user.getRegisterTeachers().add(registerTeacher);
        // gửi thông báo tới admin
        List<User> admin = userRepository.findByRoleName(PredefinedRole.ADMIN_ROLE);
        for (User a : admin) {
            NotificationEvent event = NotificationEvent.builder()
                    .recipientId(a.getId())
                    .title("Có người dùng mới đăng ký giảng viên")
                    .content("Người dùng: " + user.getFirstName() + " " + user.getLastName())
                    .targetUrl(null)
                    .type(NotificationType.REGISTER_TEACHER)
                    .createdAt(LocalDateTime.now())
                    .build();
            kafkaTemplate.send("notification-events", a.getId(), event);
        }
        return RegisterTeacherResponse.builder()
                .id(registerTeacher.getId())
                .phone(user.getPhone())
                .name(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .expertise(request.getExpertise())
//                .certificateUrl(certificateUrl)
//                .portfolioUrl(portfolioUrl)
                .status(InstructorApplicationStatus.PENDING)
                .build();
    }


    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public RegisterTeacherResponse approveTeacher(String applicationId) {

        RegisterTeacher registerTeacher = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTER_TEACHER_NOT_EXISTS));

        Role role = roleRepository.findByName(PredefinedRole.TEACHER_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTS));

        User user = registerTeacher.getUser();
        List<String> roleName = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        if(registerTeacher.getStatus().equals(InstructorApplicationStatus.PENDING) &&
                roleName.contains(PredefinedRole.USER_ROLE)) {
            registerTeacher.setStatus(InstructorApplicationStatus.APPROVED);
            user.getRoles().add(role);
            userRepository.save(user);
            applicationRepository.save(registerTeacher);

            //Thông báo cho user biết đã đăng ký thành công
            NotificationEvent event = NotificationEvent.builder()
                    .recipientId(user.getId())
                    .title("Đăng ký giảng viên đã được phê duyệt")
                    .content("Đơn xin trở thành giáo viên của bạn đã được chấp thuận.")
                    .targetUrl(null)
                    .type(NotificationType.REGISTER_TEACHER)
                    .createdAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("notification-events", user.getId(), event);

            return RegisterTeacherResponse.builder()
                    .id(registerTeacher.getId())
                    .phone(user.getPhone())
                    .name(user.getFirstName() + " " + user.getLastName())
                    .email(user.getEmail())
                    .expertise(registerTeacher.getExpertise())
                    .certificateUrl(registerTeacher.getCertificateUrl())
                    .portfolioUrl(registerTeacher.getPortfolioUrl())
                    .status(InstructorApplicationStatus.APPROVED)
                    .build();
        }
        throw new AppException(ErrorCode.REGISTER_TEACHER_INVALID);
    }


    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public RegisterTeacherResponse rejectTeacher(String applicationId) {
        RegisterTeacher registerTeacher = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTER_TEACHER_NOT_EXISTS));

        User user = registerTeacher.getUser();
        List<String> roleName = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        if (registerTeacher.getStatus().equals(InstructorApplicationStatus.PENDING) &&
                roleName.contains(PredefinedRole.USER_ROLE)) {
            registerTeacher.setStatus(InstructorApplicationStatus.REJECTED);
            applicationRepository.save(registerTeacher);

            //Thông báo cho user biết đã đăng ký thành công
            NotificationEvent event = NotificationEvent.builder()
                    .recipientId(user.getId())
                    .title("Đăng ký giảng viên đã bị từ chối")
                    .content("Đơn xin trở thành giáo viên của bạn không được chấp thuận.")
                    .targetUrl(null)
                    .type(NotificationType.REGISTER_TEACHER)
                    .createdAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("notification-events", user.getId(), event);

            return RegisterTeacherResponse.builder()
                    .id(registerTeacher.getId())
                    .phone(user.getPhone())
                    .name(user.getFirstName() + " " + user.getLastName())
                    .email(user.getEmail())
                    .expertise(registerTeacher.getExpertise())
                    .certificateUrl(registerTeacher.getCertificateUrl())
                    .portfolioUrl(registerTeacher.getPortfolioUrl())
                    .status(InstructorApplicationStatus.REJECTED)
                    .build();
        }
        throw new AppException(ErrorCode.REGISTER_TEACHER_INVALID);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(String applicationId) {
        RegisterTeacher registerTeacher = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTER_TEACHER_NOT_EXISTS));

//        if(registerTeacher.getPortfolioUrl() != null) {
//            cloudinaryService.deleteFile(cloudinaryService.extractPublicIdFromUrl(registerTeacher.getPortfolioUrl()), "raw");
//        }
//        if(registerTeacher.getCertificateUrl() != null){
//            cloudinaryService.deleteFile(cloudinaryService.extractPublicIdFromUrl(registerTeacher.getCertificateUrl()), "raw");
//        }
        applicationRepository.deleteById(applicationId);
    }

}
