package V1Learn.spring.service;


import V1Learn.spring.dto.request.CategoryCreationRequest;
import V1Learn.spring.dto.request.CategoryUpdateRequest;
import V1Learn.spring.dto.response.CategoryResponse;
import V1Learn.spring.entity.Category;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.CategoryMapper;
import V1Learn.spring.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN') and isAuthenticated()")
    public CategoryResponse createCategory(CategoryCreationRequest request) {
        validateDuplicateName(request.getName(), null);
        Category category = categoryMapper.toCategory(request);

        category = categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse) // Method Reference cực gọn
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return categoryMapper.toCategoryResponse(category);
    }


    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN') and isAuthenticated()")
    public CategoryResponse updateCategory(String id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // Update các trường từ request vào entity cũ
        categoryMapper.updateCategory(category, request);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN') and isAuthenticated()")
    public void delete(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
    }

    private boolean checkExistedName(String name, String id) {
        return categoryRepository.findExistedName(name, id) != null;
    }

    private void validateDuplicateName(String name, String id) {
        if (checkExistedName(name, id)) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
    }
}
