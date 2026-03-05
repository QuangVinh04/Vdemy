package V1Learn.spring.mapper;




import V1Learn.spring.dto.request.MediaUploadRequest;
import V1Learn.spring.dto.response.MediaFileResponse;
import V1Learn.spring.entity.MediaFile;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MediaFileMapper {


    MediaFile toMediaFile(MediaUploadRequest request);


    MediaFileResponse toMediaFileResponse(MediaFile mediaFile);


}
