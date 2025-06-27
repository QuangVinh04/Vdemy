package V1Learn.spring.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentProvider {
    @JsonProperty("vn_pay")
    VN_PAY,
    @JsonProperty("pay_os")
    PAY_OS
}
