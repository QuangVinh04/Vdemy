package V1Learn.spring.enums;

public enum OrderStatus {
    PENDING,    // Chờ xử lý
    CONFIRMED,  // Đã xác nhận
    PROCESSING, // Đang xử lý
    COMPLETED,  // Hoàn thành
    CANCELLED,  // Đã hủy
    REFUNDED    // Đã hoàn tiền
}