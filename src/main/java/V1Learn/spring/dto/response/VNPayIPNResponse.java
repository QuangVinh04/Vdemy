package V1Learn.spring.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VNPayIPNResponse {
    @JsonProperty("RspCode")
    private String rspCode;
    @JsonProperty("Message")
    private String message;
}
