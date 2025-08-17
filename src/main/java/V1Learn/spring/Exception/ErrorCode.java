package V1Learn.spring.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR), // 500
    VALIDATION_REQUIRED_FIELD(9001, "validation required field", HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_EXIST(9002, "enrollment not exist", HttpStatus.BAD_REQUEST),
    ENROLLMENT_ALREADY_COMPLETED(9003, "enrollment already completed", HttpStatus.BAD_REQUEST),
    WRONG_TYPE(9004, "Incorrect type of notification", HttpStatus.BAD_REQUEST),
    VERIFY_CODE_NOT_EXISTS(9005, "verify code not exists", HttpStatus.BAD_REQUEST),
    INVALID_VERIFY_CODE(9006, "invalid verify code", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_EXISTED(9007, "notification not existed", HttpStatus.BAD_REQUEST),

    // Authentication & Authorization Errors
    AUTH_UNAUTHORIZED   (1001, "Unauthorized", HttpStatus.UNAUTHORIZED),
    AUTH_FORBIDDEN	    (1002, "You do not have permission to access this resource", HttpStatus.FORBIDDEN),
    AUTH_INVALID_TOKEN	(1003, "Invalid token", HttpStatus.UNAUTHORIZED),
    AUTH_EXPIRED_TOKEN	(1004, "Expired token", HttpStatus.UNAUTHORIZED),
    AUTH_ACCOUNT_LOCKED	(1005, "Account locked", HttpStatus.UNAUTHORIZED),

    //User Errors
    USER_NOT_FOUND(2001, "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(2002, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_ACTIVE(2003, "User not active", HttpStatus.FORBIDDEN),
    EMAIL_INVALID(2003, "Invalid email", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTS(2004, "Email not exists", HttpStatus.BAD_REQUEST),
    REGISTER_TEACHER_NOT_EXISTS(2005, "Register teacher not exists", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTS(2006, "Role not exists", HttpStatus.BAD_REQUEST ),
    REGISTER_TEACHER_INVALID(2007, "Register teacher invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(2008, "Password invalid", HttpStatus.BAD_REQUEST),
    USER_ALREADY_BANNED(2009, "User already banned", HttpStatus.BAD_REQUEST),
    USER_NOT_BANNED(2010, "User not banned", HttpStatus.BAD_REQUEST),
    // Course Errors
    COURSE_NOT_FOUND(3001, "Course not found", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_REGISTERED(3002, "User already enrolled in this course", HttpStatus.BAD_REQUEST),
    COURSE_ACCESS_DENIED(3003, "User has no access to this course", HttpStatus.FORBIDDEN),
    REVIEW_NOT_FOUND (3004, "Review not found", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_BANNED(3005, "Course already banned", HttpStatus.BAD_REQUEST),
    COURSE_NOT_BANNED(3006, "Course not banned", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(3007, "Category not found", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_PUBLISHED(3008, "Course already published", HttpStatus.BAD_REQUEST),

    //  Chapter
    CHAPTER_NOT_FOUND(4001, "Chapter not found", HttpStatus.NOT_FOUND),
    CHAPTER_ALREADY_EXISTS(4002, "Chapter already exists", HttpStatus.BAD_REQUEST),

    //  Lesson
    LESSON_NOT_FOUND(5001, "Lesson not found", HttpStatus.NOT_FOUND),
    LESSON_ALREADY_EXISTS(5002, "Lesson already exists", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(5003, "Access denied", HttpStatus.FORBIDDEN),


    // Media
    MEDIA_NOT_FOUND(6001, "Media not found", HttpStatus.NOT_FOUND),
    MEDIA_ALREADY_EXISTS(6002, "Media already exists", HttpStatus.BAD_REQUEST),
    INVALID_TYPE(6003, "Invalid type", HttpStatus.BAD_REQUEST),
    MEDIA_IN_USE(6004, "Media in use", HttpStatus.BAD_REQUEST)
    ;



    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }


}
