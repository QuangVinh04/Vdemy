package V1Learn.spring.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MethodType {
    @JsonProperty("credit_card")
    CREDIT_CARD,
    @JsonProperty("paypal")
    PAYPAL,
    @JsonProperty("bank_transfer")
    BANK_TRANSFER
}
