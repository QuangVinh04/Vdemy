package V1Learn.spring.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60)) // Mặc định 60 phút sẽ tự động xóa cache
                .disableCachingNullValues() // Không lưu giá trị null để tiết kiệm RAM
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        // Serialize bằng JSON để vào Redis Desktop Manager đọc được dữ liệu rõ ràng
    }

    // Tùy chỉnh TTL (Thời gian sống) riêng cho từng loại Cache
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                // Chi tiết khóa học ít đổi -> Cho sống 2 tiếng
                .withCacheConfiguration("course_chapter",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofHours(2)))
                .withCacheConfiguration("course_lesson",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofHours(2)))
                // Thống kê (số học viên, rating) đổi thường xuyên hơn -> Cho sống 15 phút
                .withCacheConfiguration("course_stats",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("published_courses_page",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(30)));

    }

    // Xử lý lỗi cache không làm ảnh hưởng tới luồng chính

}
