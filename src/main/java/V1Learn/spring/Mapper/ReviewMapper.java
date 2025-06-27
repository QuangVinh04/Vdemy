package V1Learn.spring.Mapper;


import V1Learn.spring.DTO.event.ReviewRequest;
import V1Learn.spring.DTO.Response.ReviewResponse;
import V1Learn.spring.Entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReviewMapper {


    Review toReview(ReviewRequest reviewRequest);

    ReviewResponse toReviewResponse(Review review);


    void updateUser(@MappingTarget Review review, ReviewRequest request);


}
