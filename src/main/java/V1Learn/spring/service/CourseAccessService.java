package V1Learn.spring.service;


import V1Learn.spring.dto.response.*;
import V1Learn.spring.entity.Course;
import V1Learn.spring.entity.CourseAccess;
import V1Learn.spring.repository.*;
import V1Learn.spring.enums.AccessType;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseAccessService {
    
    CourseAccessRepository courseAccessRepository;
    CourseRepository courseRepository;
    
    public AccessDecision checkUserAccess(String courseId, String userId) {
        log.info("Checking user access for courseId: {} and userId: {}", courseId, userId);


        if (userId == null || userId.isEmpty()) {
            return new AccessDecision(false, false, "ANONYMOUS");
        }
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Check CourseAccess table
        Optional<CourseAccess> courseAccess = courseAccessRepository
                .findValidAccess(userId, courseId);

        if (courseAccess.isPresent()) {
            CourseAccess access = courseAccess.get();
            boolean isInstructor = access.getAccessType() == AccessType.INSTRUCTOR;

            log.debug("Found CourseAccess: {} for user: {}", access.getAccessType(), userId);
            return new AccessDecision(true, isInstructor, access.getAccessType().toString());
        }

        // Fallback checks
        if (SecurityUtils.hasRole("ADMIN")) {
            return new AccessDecision(true, false, "ADMIN");
        }

        if (course.getInstructor().getId().equals(userId)) {
            return new AccessDecision(true, true, "INSTRUCTOR");
        }

        if (course.getPrice().equals(BigDecimal.ZERO)) {
            return new AccessDecision(true, false, "FREE");
        }

        return new AccessDecision(false, false, "PREVIEW_ONLY");
    }

    public AccessInfo buildAccessInfo(AccessDecision accessResult, Course course) {
        List<String> availableActions = new ArrayList<>();

        if (accessResult.isHasFullAccess()) {
            availableActions.add("START_LEARNING");
        } else {
            availableActions.add("ENROLL");
            if (course.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                availableActions.add("PURCHASE");
            }
        }

        return AccessInfo.builder()
                .hasAccess(accessResult.isHasFullAccess())
                .isInstructor(accessResult.isInstructor())
                .accessType(accessResult.getAccessType())
                .availableActions(availableActions)
                .build();
    }

    

    /**
     * Kiểm tra user đã mua course chưa 
     */
    public boolean hasPurchasedAccess(String userId, String courseId) {
        Optional<CourseAccess> access = courseAccessRepository.findValidAccess(userId, courseId);
        return access.isPresent() 
                && access.get().isActive() 
                && access.get().getAccessType() == AccessType.PURCHASED;
    }

    public void grantAccess(String courseId, String userId, AccessType accessType) {
        // Kiểm tra đã có access chưa để tránh duplicate
        Optional<CourseAccess> existing = courseAccessRepository.findValidAccess(userId, courseId);
        
        if (existing.isPresent()) {
            CourseAccess access = existing.get();
            if (access.isActive() && access.getAccessType() == AccessType.PURCHASED) {
                log.warn("User {} already has PURCHASED access to course {}", userId, courseId);
                return;
            }
            // Nếu có nhưng inactive, activate lại
            access.setActive(true);
            access.setAccessType(AccessType.PURCHASED);
            access.setGrantedAt(LocalDateTime.now());
            courseAccessRepository.save(access);
            log.info("Reactivated access for user {} to course {}", userId, courseId);
            return;
        }

        CourseAccess newAccess = CourseAccess.builder()
                .userId(userId)
                .courseId(courseId)
                .accessType(accessType)
                .isActive(true)
                .grantedAt(LocalDateTime.now())
                .build();
        
        courseAccessRepository.save(newAccess);
        log.info("Granted PURCHASED access to user {} for course {}", userId, courseId);
    }

}

