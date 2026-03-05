package V1Learn.spring.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;


import java.util.HashMap;
import java.util.Map;


@Configuration
@Slf4j(topic = "KAFKA-PRODUCER")
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;



    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // Bật batching
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10); // Đợi 10ms để tích lũy batch
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // Nén dữ liệu
//        if ("prod".equals(activeProfile)) {
//            configProps.put("security.protocol", "SSL");
//            configProps.put("ssl.truststore.type", "none");
//            configProps.put("endpoint.identification.algorithm", "");
//        }

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    // topic xử lý phần logic review
    @Bean
    public NewTopic courseReview() {
        return TopicBuilder.name("course-review")
                .partitions(3)
                .replicas(3)
                .config("retention.ms", "2592000000") // giữ 30 ngày
                .build();
    }

    // topic xử lý các loại thông báo
    @Bean
    public NewTopic notificationEvent() {
        return TopicBuilder.name("notification-events")
                .partitions(3)
                .replicas(3)
                .config("retention.ms", "2592000000") // giữ 30 ngày
                .build();
    }

}
