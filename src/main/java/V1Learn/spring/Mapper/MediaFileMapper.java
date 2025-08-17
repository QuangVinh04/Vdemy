package V1Learn.spring.Mapper;




import V1Learn.spring.DTO.Request.MediaUploadRequest;
import V1Learn.spring.DTO.Response.MediaFileResponse;
import V1Learn.spring.Entity.MediaFile;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MediaFileMapper {


    MediaFile toMediaFile(MediaUploadRequest request);


    MediaFileResponse toMediaFileResponse(MediaFile mediaFile);


}
