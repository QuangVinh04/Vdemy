package V1Learn.spring.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum InstructorApplicationStatus {
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("approved")
    APPROVED,
    @JsonProperty("rejected")
    REJECTED
}