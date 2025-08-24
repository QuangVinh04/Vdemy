package V1Learn.spring.Service;


import V1Learn.spring.DTO.Request.CreateSignatureRequest;
import V1Learn.spring.DTO.Response.CreateSignatureResponse;
import V1Learn.spring.config.CloudinaryConfig;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {
    Cloudinary cloudinary;


    /**
     * Xóa file khỏi Cloudinary
     * @param publicId ID file trên Cloudinary
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean delete(String publicId, String contentType) {
        try {
            Map<String, Object> deleteResult = cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", contentType));
            return "ok".equals(deleteResult.get("result"));
        } catch (IOException e) {
            throw new RuntimeException("Lỗi xóa file: " + e.getMessage());
        }
    }



    public CreateSignatureResponse createSignature(CreateSignatureRequest req) {
        long ts = System.currentTimeMillis() / 1000L;
        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", ts);
        params.put("folder", req.getFolder());
        // có thể thêm thông số khác nếu cần, nhưng ký tối giản là đủ
        String sig = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);

        return new CreateSignatureResponse(
                cloudinary.config.cloudName,
                cloudinary.config.apiKey,
                req.getFolder(),
                req.getResourceType(),
                ts,
                sig
        );
    }
}
