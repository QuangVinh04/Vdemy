package V1Learn.spring.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentStatus {
    PENDING,    // Chờ thanh toán
    PAID,       // Đã thanh toán
    FAILED,     // Thanh toán thất bại
    REFUNDED    // Đã hoàn tiền
}