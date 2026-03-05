package V1Learn.spring.service;

import V1Learn.spring.dto.request.CartCreationRequest;
import V1Learn.spring.dto.response.CartResponse;
import V1Learn.spring.entity.Cart;
import V1Learn.spring.entity.CartItem;
import V1Learn.spring.entity.Course;
import V1Learn.spring.entity.OrderItem;
import V1Learn.spring.entity.User;
import V1Learn.spring.enums.CourseStatus;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.CartMapper;
import V1Learn.spring.repository.CartItemRepository;
import V1Learn.spring.repository.CartRepository;
import V1Learn.spring.repository.CourseRepository;
import V1Learn.spring.repository.UserRepository;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    CartMapper cartMapper;
    CourseRepository courseRepository;
    UserRepository userRepository;
    EnrollmentService enrollmentService;
    CourseAccessService courseAccessService;

    @Transactional
    public CartResponse addToCart(CartCreationRequest request) {

        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        validatCourseForCart(user, course);

        Cart cart = performAddCart(user, course);

        return cartMapper.toCartResponse(cart);
    }

    private void validatCourseForCart(User user, Course course) {
        if (!course.getStatus().equals(CourseStatus.PUBLISHED)) {
            throw new AppException(ErrorCode.COURSE_NOT_AVAILABLE);
        }

        // 2. ✅ SỬA: Kiểm tra user đã có quyền truy cập (đã mua) chưa - dùng
        // CourseAccess
        if (courseAccessService.hasPurchasedAccess(user.getId(), course.getId())) {
            throw new AppException(ErrorCode.COURSE_ALREADY_OWNED);
        }

        // 3. Kiểm tra user không phải là instructor của course
        if (course.getInstructor().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.COURSE_CANNOT_ADD_OWN_COURSE_TO_CART);
        }
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public CartResponse getCart(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(user));
        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void removeCourseFromCart(String userId, String courseId) {
        cartItemRepository.deleteByCartUserIdAndCourseId(userId, courseId);
    }

    private Cart performAddCart(User user, Course course) {
        return cartRepository.findByUserId(user.getId())
                .map(existingCart -> addOrUpdateCartItem(existingCart, course))
                .orElseGet(() -> createNewCart(user));
    }

    private Cart createNewCart(User user) {
        return Cart.builder()
                .user(user)
                .build();
    }

    private Cart addOrUpdateCartItem(Cart cart, Course course) {
        boolean exists = cart.getItems().stream()
                .anyMatch(item -> item.getCourse().getId().equals(course.getId()));
        if (!exists) {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .course(course)
                    .build();
            cartItemRepository.save(item);
            cart.getItems().add(item);
            return cartRepository.save(cart);
        }
        return cart;
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void clearCart(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (cart != null && cart.getItems() != null && !cart.getItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
            cartRepository.save(cart);
            log.info("Cleared cart for user: {}", userId);
        }
    }

    /**
     * Xóa cart items sau khi payment thành công
     */
    void clearCartAfterPayment(String userId, Set<OrderItem> orderItems) {
        try {
            Cart cart = cartRepository.findByUserId(userId).orElse(null);
            if (cart != null) {
                // Xóa các cart items tương ứng với order items
                orderItems.forEach(orderItem -> {
                    cartItemRepository.deleteByCartUserIdAndCourseId(userId, orderItem.getCourseId());
                });
                log.info("Cleared cart items for user {} after successful payment", userId);
            }
        } catch (Exception e) {
            log.error("Error clearing cart for user {}: {}", userId, e.getMessage());
            // Không throw exception vì payment đã thành công, chỉ log error
        }
    }

}
