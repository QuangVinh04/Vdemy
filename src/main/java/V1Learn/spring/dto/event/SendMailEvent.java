package V1Learn.spring.dto.event;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendMailEvent {
    private String email;
    private String subject;
    private String templateName;
    private String otpCode;
}
