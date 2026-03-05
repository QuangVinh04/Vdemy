package V1Learn.spring.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR), // 500
    VALIDATION_REQUIRED_FIELD(9001, "validation required field", HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_EXIST(9002, "enrollment not exist", HttpStatus.BAD_REQUEST),
    ENROLLMENT_ALREADY_EXISTS(9003, "enrollment already exists", HttpStatus.BAD_REQUEST),
    WRONG_TYPE(9004, "Incorrect type of notification", HttpStatus.BAD_REQUEST),
    VERIFY_CODE_NOT_EXISTS(9005, "verify code not exists", HttpStatus.BAD_REQUEST),
    INVALID_VERIFY_CODE(9006, "invalid verify code", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_EXISTED(9007, "notification not existed", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(9008, "Invalid request", HttpStatus.BAD_REQUEST),

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
    EMAIL_INVALID(2004, "Invalid email", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTS(2005, "Email not exists", HttpStatus.BAD_REQUEST),
    REGISTER_TEACHER_NOT_EXISTS(2006, "Register teacher not exists", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTS(2007, "Role not exists", HttpStatus.BAD_REQUEST ),
    REGISTER_TEACHER_INVALID(2008, "Register teacher invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(2009, "Password invalid", HttpStatus.BAD_REQUEST),
    USER_ALREADY_BANNED(2010, "User already banned", HttpStatus.BAD_REQUEST),
    USER_NOT_BANNED(2011, "User not banned", HttpStatus.BAD_REQUEST),

    // Course Errors
    COURSE_NOT_FOUND(3001, "Course not found", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_REGISTERED(3002, "User already enrolled in this course", HttpStatus.BAD_REQUEST),
    COURSE_ACCESS_DENIED(3003, "User has no access to this course", HttpStatus.FORBIDDEN),
    COURSE_ALREADY_BANNED(3004, "Course already banned", HttpStatus.BAD_REQUEST),
    COURSE_NOT_BANNED(3005, "Course not banned", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(3006, "Category not found", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_PUBLISHED(3007, "Course already published", HttpStatus.BAD_REQUEST),
    COURSE_NOT_AVAILABLE(3008, "Course not available", HttpStatus.BAD_REQUEST),
    COURSE_ALREADY_OWNED(3009, "Course already owned", HttpStatus.BAD_REQUEST),
    COURSE_CANNOT_ADD_OWN_COURSE_TO_CART(3010, "Cannot add own course to cart", HttpStatus.BAD_REQUEST),
    // Review
    REVIEW_INVALID_RATING(3011, "Invalid rating", HttpStatus.BAD_REQUEST),
    REVIEW_CONTENT_REQUIRED(3012, "Content is required", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND(3013, "Review not found", HttpStatus.NOT_FOUND),
    
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
    MEDIA_IN_USE(6004, "Media in use", HttpStatus.BAD_REQUEST),


    // Order
    CHECKOUT_NOT_FOUND(7001, "Checkout not found" , HttpStatus.BAD_REQUEST),
    CHECKOUT_ALREADY_USED(7002, "Checkout already used", HttpStatus.BAD_REQUEST),
    CHECKOUT_INVALID_STATE(7004, "Checkout invalid state", HttpStatus.BAD_REQUEST),
    AMOUNT_MISMATCH(7005,"Amount mismatch" , HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(7006,"Order not found" , HttpStatus.BAD_REQUEST),
    CANNOT_CHECKOUT_OWN_COURSE(7007, "Cannot checkout own course", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_BE_CANCELLED(7008, "Order cannot be cancelled", HttpStatus.BAD_REQUEST),
    
    //Payment
    PAYMENT_NOT_FOUND(8001, "Payment not found", HttpStatus.NOT_FOUND),




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
