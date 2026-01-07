package com.taaply.todo.categories;

import com.taaply.todo.auth.UserPrincipal;
import com.taaply.todo.categories.dto.CategoryResponse;
import com.taaply.todo.categories.dto.CreateCategoryRequest;
import com.taaply.todo.categories.dto.UpdateCategoryRequest;
import com.taaply.todo.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<CategoryResponse> categories = categoryService.getAllCategories(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {

        CategoryResponse category = categoryService.getCategoryById(id, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        CategoryResponse category = categoryService.createCategory(request, principal.getUserId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        CategoryResponse category = categoryService.updateCategory(id, request, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {

        categoryService.deleteCategory(id, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }
}
