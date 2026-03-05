package V1Learn.spring.mapper;

import V1Learn.spring.dto.response.NotificationResponse;
import V1Learn.spring.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toNotificationResponse(Notification notification);


}
