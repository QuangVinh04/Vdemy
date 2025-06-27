package V1Learn.spring.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentStatus {
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("completed")
    COMPLETED,
    @JsonProperty("failed")
    FAILED
}