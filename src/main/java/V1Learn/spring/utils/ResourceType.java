package V1Learn.spring.utils;

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
