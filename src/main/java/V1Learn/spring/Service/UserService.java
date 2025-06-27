package V1Learn.spring.Service;

import V1Learn.spring.DTO.Request.*;
import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.DTO.Response.UserEnrolledResponse;
import V1Learn.spring.DTO.Response.UserResponse;
import V1Learn.spring.Entity.Enrollment;
import V1Learn.spring.Entity.Role;
import V1Learn.spring.Entity.User;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Mapper.UserMapper;
import V1Learn.spring.Repostiory.EnrollmentRepository;
import V1Learn.spring.Repostiory.RoleRepository;
import V1Learn.spring.Repostiory.UserRepository;
import V1Learn.spring.constant.PredefinedRole;
import V1Learn.spring.utils.EnrollmentStatus;
import V1Learn.spring.utils.SecurityUtils;
import V1Learn.spring.utils.UserStatus;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    EnrollmentRepository enrollmentRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    MailService mailService;
    RedisService redisService;

    @NonFinal
    String CONFIRM_USER = "confirm_user";

    @NonFinal
    String RESET_PASSWORD_CODE = "reset_password_code";

    @Transactional
    public void createUser(UserCreationRequest request) throws MessagingException, UnsupportedEncodingException {
        log.info("Service: create User");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }
        HashSet<Role> roles = new HashSet<>();
        //Phương thức findById() trả về một Optional<Role> (nếu tìm thấy role thì giá trị sẽ có mặt, nếu không tìm thấy thì sẽ là Optional.empty()).
        //.ifPresent(roles::add): Nếu role tồn tại (isPresent()), thì nó sẽ được thêm vào danh sách roles.
        roleRepository.findByName(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);
        user.setStatus(UserStatus.INACTIVE);

        // Tạo mã xác nhận ngẫu nhiên
        String verifyCode = mailService.generateVerifyCode();

        // lưu mã vào redis có thời hạn 10p
        redisService.setWithTTL(CONFIRM_USER + request.getEmail(), verifyCode, System.currentTimeMillis() + 600000, TimeUnit.MILLISECONDS); // 10 phút
        // lưu tạm tài khoản tạm thời khóa chờ xác nhận để kích hoạt
        userRepository.save(user);

        mailService.sendConfirmLink(request.getEmail(), verifyCode);
    }

    @Transactional
    public UserResponse confirmUser(VerifyOtpRequest request) {
        // nếu mã hết hạn
        if(!redisService.existsValue(CONFIRM_USER + request.getEmail())) {
            // Tìm user trong database với email và trạng thái NONE
            User user = userRepository.findByEmailAndStatus(request.getEmail(), UserStatus.NONE)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            userRepository.delete(user);
            log.info("Deleted unconfirmed user with email={} due to expired verification code", request.getEmail());

            throw new AppException(ErrorCode.VERIFY_CODE_NOT_EXISTS);
        }

        if(verifyOtp(request, CONFIRM_USER)) {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() ->new AppException(ErrorCode.USER_NOT_FOUND));
            // mã xác nhận đúng kích hoạt tài khoản
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
            // Xóa mã xác nhận khỏi Redis sau khi xác nhận thành công
            redisService.deleteValue(CONFIRM_USER + request.getEmail());
            UserResponse userResponse = userMapper.toUserResponse(user);
            userResponse.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
            return userResponse;
        }


        throw new AppException(ErrorCode.INVALID_VERIFY_CODE); // mã sai
    }


    @Transactional
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public void createPassword(PasswordCreationFirstRequest request){

        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(StringUtils.hasText(user.getPassword()))
            throw new AppException(ErrorCode.PASSWORD_INVALID);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }



    @Transactional
    public void sendOtpForgotPassword(EmailRequest request) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new AppException(ErrorCode.USER_NOT_ACTIVE);
        }
        String otp = mailService.generateVerifyCode();
        redisService.setWithTTL(RESET_PASSWORD_CODE + request.getEmail(), otp, System.currentTimeMillis() + 600000, TimeUnit.MILLISECONDS); // 10 phút
        mailService.sendConfirmLink(request.getEmail(), otp);
        log.info("{}{}", RESET_PASSWORD_CODE, request.getEmail());
        log.info(otp);
    }



    @Transactional
    public void resetPassword(ResetPasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        VerifyOtpRequest verifyRequest = VerifyOtpRequest.builder()
                .email(request.getEmail())
                .otp(request.getOtp())
                .build();
        if(!verifyOtp(verifyRequest, RESET_PASSWORD_CODE)) {
            throw new AppException(ErrorCode.INVALID_VERIFY_CODE);

        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        redisService.deleteValue(RESET_PASSWORD_CODE + request.getEmail());
        userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void changePassword(ChangePasswordRequest request){
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        if(!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public boolean verifyOtp(VerifyOtpRequest request, String key){
        if(!userRepository.existsByEmail(request.getEmail()) || request.getEmail() == null){
            return false;
        }

        if(!redisService.existsValue(key + request.getEmail())){
            return false;
        }
        String verifyCode = redisService.getValue(key + request.getEmail()).toString();
        return request.getOtp().equals(verifyCode);
    }



    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponse getUserById(String id) {
        log.info("In method get user by id");
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponse> getAllUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    public PageResponse getAllStudentsByTeacher(Pageable pageable) {
        log.info("In method get Students by Teacher");
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Page<UserEnrolledResponse> responses = enrollmentRepository.findAllByInstructorId(userId, EnrollmentStatus.COMPLETED, pageable);

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(responses.getTotalPages())
                .items(responses.toList())
                .build();
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    public void deleteUserByTeacher(String userId) {
        log.info("In method get Students by Teacher");
        String teacherId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Enrollment  enrollment = enrollmentRepository.findByUserIdAndInstructorId(userId, teacherId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXIST));

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);

    }



    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }


    @PreAuthorize("isAuthenticated()")
    public UserResponse getMyInfo() {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return userResponse;
    }

    public boolean checkUserExists(EmailRequest request) {
        return userRepository.existsByEmail(request.getEmail());
    }


}
