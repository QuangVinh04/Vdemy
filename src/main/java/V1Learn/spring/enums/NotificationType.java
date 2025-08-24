package V1Learn.spring.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NotificationType {
    @JsonProperty("course_review")
    COURSE_REVIEW,
    @JsonProperty("review_reply")
    REVIEW_REPLY,
    @JsonProperty("order_confirmed")
    ORDER_CONFIRMED,
    @JsonProperty("register_teacher")
    REGISTER_TEACHER,

}
