package V1Learn.spring.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HeartbeatRequest {

    @NotNull(message = "POSITION_REQUIRED")
    @Min(value = 0, message = "INVALID_TIME_VALUE")
    Integer lastWatchedSecond;
}
