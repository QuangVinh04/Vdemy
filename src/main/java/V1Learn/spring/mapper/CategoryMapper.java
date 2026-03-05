package V1Learn.spring.mapper;




import V1Learn.spring.dto.request.CartCreationRequest;
import V1Learn.spring.dto.request.CategoryCreationRequest;
import V1Learn.spring.dto.request.CategoryUpdateRequest;
import V1Learn.spring.dto.response.CategoryResponse;
import V1Learn.spring.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "isActive", constant = "true")
    Category toCategory(CategoryCreationRequest request);

    CategoryResponse toCategoryResponse(Category category);

    void updateCategory(@MappingTarget Category category, CategoryUpdateRequest request);
}
