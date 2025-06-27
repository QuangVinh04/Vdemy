package V1Learn.spring.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisService {

    RedisTemplate<String, Object> redisTemplate;

    // lưu dữ liệu vào Redis
    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // Lưu dữ liệu vào Redis với thời gian hết hạn (TTL)
    // timeout: là thời gian dữ liệu tồn tại trong Redis
    // timeunit: đơn vị thời gian của timeout
    public void setWithTTL(String key, Object value, long timeout, TimeUnit unit) {
        log.info("timeout = {} and unit = {}", timeout, unit);
        timeout = Math.max(timeout - System.currentTimeMillis(), 0); // tính thời gian hết hạn từ lúc hệ thống tạo ra
        if (timeout <= 0 || unit == null) {
            throw new IllegalArgumentException("Invalid timeout or unit");
        }
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // Lấy dữ liệu từ Redis
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Xóa dữ liệu từ Redis
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    // Kiểm tra xem key có tồn tại trong value không
    public boolean existsValue(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
