# 🎓 Vdemy — Online Course Platform

Nền tảng học trực tuyến được xây dựng bằng **Spring Boot**, hỗ trợ quản lý khóa học, thanh toán VNPay, đăng nhập Google OAuth2 và thông báo real-time.

## Công nghệ sử dụng

- **Backend**: Spring Boot 3.3.4 · Java 22 · Spring Security 6 · Spring Data JPA
- **Database**: MySQL 8.0 · Redis 6.2 (caching)
- **Messaging**: Apache Kafka 3.7 · WebSocket (STOMP)
- **Auth**: JWT + Google OAuth2
- **Payment**: VNPay Gateway
- **Storage**: Cloudinary (media upload)
- **Docs**: SpringDoc OpenAPI (Swagger UI)
- **DevOps**: Docker · Docker Compose

## Tính năng chính

- � Đăng ký / Đăng nhập (Email + Google OAuth2), phân quyền (Admin, Instructor, Student)
- 📚 CRUD khóa học (Course → Chapter → Lesson), upload video qua Cloudinary
- 🛒 Giỏ hàng, Checkout, Đơn hàng, Thanh toán VNPay (idempotent)
- 📝 Ghi danh tự động, theo dõi tiến trình học tập
- ⭐ Đánh giá & review khóa học
- 🔔 Thông báo real-time (WebSocket + Kafka)
- 📧 Gửi email xác thực & nhắc nhở (Thymeleaf templates)
- 🏫 Đăng ký giảng viên & Admin duyệt
- � Thống kê doanh thu

## Cài đặt

### 1. Cấu hình môi trường

```bash
cp .env.example .env
# Điền các giá trị: MySQL, Google OAuth2, JWT, Cloudinary, VNPay, Mail
```

### 2. Chạy với Docker

```bash
docker-compose up -d
```

### 3. Chạy Development

```bash
# Khởi động infrastructure
docker-compose up -d mysql redis kafka

# Chuyển môi trường & chạy app
switch-env.bat dev
./mvnw spring-boot:run
```

Ứng dụng chạy tại: **http://localhost:8081/vinh**

API Docs: **http://localhost:8081/vinh/swagger-ui/index.html**

## Cấu trúc dự án

```
src/main/java/V1Learn/spring/
├── config/          # Security, Redis, Kafka, Cloudinary, WebSocket
├── controller/      # 18 REST Controllers + admin/
├── dto/             # Request / Response DTOs
├── entity/          # 22 JPA Entities
├── enums/           # Status, Role, Payment enums
├── exception/       # Global Exception Handling
├── mapper/          # MapStruct Mappers
├── repository/      # Spring Data JPA Repositories
├── service/         # Business Logic + handler/ (Strategy Pattern)
├── schedule/        # Scheduled Tasks
└── security/        # Custom UserDetailsService
```

## Docker Compose

| Service  | Image                 | Port         |
|----------|-----------------------|--------------|
| app      | Spring Boot           | `8081:8081`  |
| mysql    | `mysql:8.0`           | `3307:3306`  |
| redis    | `redis:6.2-alpine`    | `6379:6379`  |
| kafka    | `bitnami/kafka:3.7.0` | `9094:9094`  |

## Tác giả

**Quang Vinh** — [GitHub](https://github.com/QuangVinh04)
