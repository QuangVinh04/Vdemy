package V1Learn.spring.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum InstructorApplicationStatus {
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("approved")
    APPROVED,
    @JsonProperty("rejected")
    REJECTED
}