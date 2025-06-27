package V1Learn.spring.Service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {
    Cloudinary cloudinary;

    /**
     * Upload ảnh đại diện (avatar) cho người dùng
     * @param file Ảnh tải lên
     * @return URL ảnh trên Cloudinary
     */
    public String uploadUserAvatar(MultipartFile file) {
        return uploadFile(file, "image", "user_avatars");
    }

    /**
     * Upload ảnh/video khóa học
     * @param file Ảnh/Video tải lên
     * @param isVideo Nếu true, upload video; nếu false, upload ảnh
     * @return URL file sau khi upload
     */
    public String uploadCourseMedia(MultipartFile file, boolean isVideo) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }
        String resourceType = isVideo ? "video" : "image";
        String folder = isVideo ? "course_videos" : "course_images";
        return uploadFile(file, resourceType, folder);
    }

    public String uploadLesson(MultipartFile file, String contentType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }
        return uploadFile(file, contentType, "lesson_" + contentType);
    }

    public String upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }
        return uploadFile(file, "raw", folder);
    }




    /**
     * Xóa file khỏi Cloudinary
     * @param publicId ID file trên Cloudinary
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteFile(String publicId, String contentType) {
        try {
            Map<String, Object> deleteResult = cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", contentType));
            return "ok".equals(deleteResult.get("result"));
        } catch (IOException e) {
            throw new RuntimeException("Lỗi xóa file: " + e.getMessage());
        }
    }

    /**
     * Xử lý upload file lên Cloudinary
     * @param file File tải lên
     * @param resourceType Loại tài nguyên (image/video)
     * @param folder Thư mục lưu trữ trên Cloudinary
     * @return URL file sau khi upload
     */
    private String uploadFile(MultipartFile file, String resourceType, String folder) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", resourceType,
                            "use_filename", true,
                            "unique_filename", true
                    ));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload file: " + e.getMessage());
        }
    }


    public String extractPublicIdFromUrl(String url) {
        // Tìm vị trí sau "/upload/"
        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex == -1) return null;

        String afterUpload = url.substring(uploadIndex + 8); // bỏ qua "/upload/"

        // Bỏ phần version (vd: v1234567890/)
        String[] parts = afterUpload.split("/");
        int startIndex = parts[0].startsWith("v") ? 1 : 0;

        // Ghép lại publicId và bỏ đuôi mở rộng
        StringBuilder publicIdBuilder = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) {
            publicIdBuilder.append(parts[i]);
            if (i < parts.length - 1) publicIdBuilder.append("/");
        }

        String publicIdWithExt = publicIdBuilder.toString();
        int dotIndex = publicIdWithExt.lastIndexOf('.');
        return dotIndex != -1 ? publicIdWithExt.substring(0, dotIndex) : publicIdWithExt;
    }
}
