package V1Learn.spring.Service;


import V1Learn.spring.DTO.Response.*;
import V1Learn.spring.Entity.Chapter;
import V1Learn.spring.Entity.Course;
import V1Learn.spring.Entity.CourseAccess;
import V1Learn.spring.Entity.LessonProgress;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Repostiory.*;
import V1Learn.spring.enums.AccessType;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseAccessService {
    
    CourseAccessRepository courseAccessRepository;

    public AccessDecision checkUserAccess(String courseId, Course course, String userId) {
        log.debug("Checking user access for course: {}", courseId);
        

        if (userId == null || userId.isEmpty()) {
            return new AccessDecision(false, false, "ANONYMOUS");
        }

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

}

