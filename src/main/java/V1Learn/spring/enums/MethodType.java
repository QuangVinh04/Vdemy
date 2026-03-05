package V1Learn.spring.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MethodType {
    @JsonProperty("credit_card")
    CREDIT_CARD,
    @JsonProperty("paypal")
    PAYPAL,
    @JsonProperty("bank_transfer")
    BANK_TRANSFER
}
