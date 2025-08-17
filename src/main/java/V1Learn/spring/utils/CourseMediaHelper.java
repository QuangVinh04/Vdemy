//package V1Learn.spring.utils;
//
//
//import V1Learn.spring.Entity.Course;
//import V1Learn.spring.Service.CloudinaryService;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class CourseMediaHelper {
//
//    CloudinaryService cloudinaryService;
//
//    public void handleMediaUpload(Course course, MultipartFile thumbnail, MultipartFile video) {
//        if (thumbnail != null) {
//            if (course.getThumbnailUrl() != null) {
//                cloudinaryService.deleteFile(course.getThumbnailUrl(), "image");
//            }
//            String url = cloudinaryService.uploadCourseMedia(thumbnail, false);
//            course.setThumbnailUrl(url);
//        }
//
//        if (video != null) {
//            if (course.getVideoUrl() != null) {
//                cloudinaryService.deleteFile(course.getVideoUrl(), "video");
//            }
//            String url = cloudinaryService.uploadCourseMedia(video, true);
//            course.setVideoUrl(url);
//        }
//    }
//}