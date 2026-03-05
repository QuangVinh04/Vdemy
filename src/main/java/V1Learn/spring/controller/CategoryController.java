package V1Learn.spring.controller;


import V1Learn.spring.dto.request.CartCreationRequest;
import V1Learn.spring.dto.request.CategoryCreationRequest;
import V1Learn.spring.dto.request.CategoryUpdateRequest;
import V1Learn.spring.dto.response.APIResponse;
import V1Learn.spring.dto.response.CartResponse;
import V1Learn.spring.dto.response.CategoryResponse;
import V1Learn.spring.service.CartService;
import V1Learn.spring.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/category")
@Slf4j
public class CategoryController {

    CategoryService categoryService;

    @GetMapping("/all")
    public APIResponse<List<CategoryResponse>> getAllCategory() {

        return APIResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getAllCategories())
                .message("get all category successfully")
                .build();
    }
    @GetMapping("/{categoryId}")
    public APIResponse<CategoryResponse> getCategoryById(@PathVariable String categoryId) {

        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.getCategoryById(categoryId))
                .message("get category successfully")
                .build();
    }

    @PostMapping("/create")
    public APIResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryCreationRequest request) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .message("create category new successfully")
                .build();
    }

    @PostMapping("/update/{categoryId}")
    public APIResponse<CategoryResponse> updateCategory(@PathVariable String categoryId,
                                                        @RequestBody @Valid CategoryUpdateRequest request) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(categoryId, request))
                .message("update category new successfully")
                .build();
    }

    @DeleteMapping("/delete/{categoryId}")
    public APIResponse<Void> deleteCategory(@PathVariable String categoryId) {
        categoryService.delete(categoryId);
        return APIResponse.<Void>builder()
                .message("delete category successfully")
                .build();
    }



}
