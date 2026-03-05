package V1Learn.spring.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentMethod {
    @JsonProperty("vn_pay")
    VN_PAY,
    @JsonProperty("pay_os")
    PAY_OS
}
