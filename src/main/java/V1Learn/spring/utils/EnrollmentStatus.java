package V1Learn.spring.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EnrollmentStatus {
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("completed")
    COMPLETED,
    @JsonProperty("cancelled")
    CANCELLED;
}