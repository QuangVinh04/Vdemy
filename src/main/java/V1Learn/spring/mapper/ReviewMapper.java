package V1Learn.spring.mapper;


import V1Learn.spring.dto.event.ReviewRequest;
import V1Learn.spring.dto.response.ReviewResponse;
import V1Learn.spring.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReviewMapper {


    Review toReview(ReviewRequest reviewRequest);

    ReviewResponse toReviewResponse(Review review);


    void updateUser(@MappingTarget Review review, ReviewRequest request);


}
