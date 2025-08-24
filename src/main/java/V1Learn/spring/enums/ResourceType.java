package V1Learn.spring.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResourceType {
    @JsonProperty("image")
    IMAGE,
    @JsonProperty("video")
    VIDEO,
    @JsonProperty("raw")
    RAW,
    @JsonProperty("auto")
    AUTO;
}
