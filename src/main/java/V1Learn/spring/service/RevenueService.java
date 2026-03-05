package V1Learn.spring.service;


import V1Learn.spring.dto.response.DashboardSummaryResponse;
import V1Learn.spring.dto.response.MonthlyRevenueResponse;
import V1Learn.spring.dto.response.OrderEarningResponse;
import V1Learn.spring.entity.User;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.repository.CourseRepository;
import V1Learn.spring.repository.PaymentRepository;
import V1Learn.spring.repository.UserRepository;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RevenueService {
    UserRepository userRepository;
    PaymentRepository paymentRepository;
    CourseRepository courseRepository;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public DashboardSummaryResponse getDashboardSummary(int month, int year) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

//        BigDecimal revenueThisMonth = paymentRepository
//                .findByUserAndDateRange(user.getId(), start, end, PaymentStatus.PAID)
//                .stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Double ratingThisMonth = courseRepository
                .getAverageRatingByUserAndMonth(user.getId(), month, year);

//        int newStudentsThisMonth = userRepository
//                .countNewStudentsByUserAndMonth(user.getId(), month, year, EnrollmentStatus.COMPLETED);

        return DashboardSummaryResponse.builder()
                .revenueThisMonth(null)
                .ratingThisMonth(ratingThisMonth)
                .newStudentsThisMonth(null)
                .build();
    }


    // 2. API doanh thu theo tháng trong năm
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public List<MonthlyRevenueResponse> getMonthlyRevenue(int year) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<MonthlyRevenueResponse> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime end = start.plusMonths(1).minusSeconds(1);
//            BigDecimal revenue = paymentRepository
//                    .findByUserAndDateRange(user.getId(), start, end, PaymentStatus.COMPLETED)
//                    .stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            result.add(new MonthlyRevenueResponse(month, null));
        }
        return result;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public List<OrderEarningResponse> getOrderEarnings(LocalDate from, LocalDate to) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);

//        List<Payment> payments = paymentRepository.findByUserAndDateRange(user.getId(), start, end, PaymentStatus.COMPLETED);
        List<OrderEarningResponse> result = new ArrayList<>();
//        for (Payment p : payments) {
//            result.add(OrderEarningResponse.builder()
//                    .orderId(p.getId())
//                    .date(p.getCreatedAT().toLocalDate())
//                    .course(p.getEnrollment().getCourse().getTitle())
//                    .amount(p.getAmount())
//                    .build());
//        }
        return result;
    }
}