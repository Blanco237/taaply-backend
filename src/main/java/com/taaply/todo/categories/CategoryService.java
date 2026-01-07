package com.taaply.todo.categories;

import com.taaply.todo.categories.dto.CategoryResponse;
import com.taaply.todo.categories.dto.CreateCategoryRequest;
import com.taaply.todo.categories.dto.UpdateCategoryRequest;
import com.taaply.todo.common.exceptions.ForbiddenException;
import com.taaply.todo.common.exceptions.ResourceNotFoundException;
import com.taaply.todo.users.UserEntity;
import com.taaply.todo.users.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(UUID userId) {
        return categoryRepository.findByUserIdOrderByCreatedAt(userId)
                .stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID categoryId, UUID userId) {
        CategoryEntity category = findCategoryAndValidateOwnership(categoryId, userId);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request, UUID userId) {
        UserEntity user = userService.findById(userId);

        CategoryEntity category = CategoryEntity.builder()
                .name(request.getName())
                .color(request.getColor())
                .user(user)
                .build();

        CategoryEntity savedCategory = categoryRepository.save(category);
        log.info("Created category: {} for user: {}", savedCategory.getId(), userId);

        return CategoryResponse.fromEntity(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(UUID categoryId, UpdateCategoryRequest request, UUID userId) {
        CategoryEntity category = findCategoryAndValidateOwnership(categoryId, userId);

        if (request.getName() != null && !request.getName().isBlank()) {
            category.setName(request.getName());
        }
        if (request.getColor() != null && !request.getColor().isBlank()) {
            category.setColor(request.getColor());
        }

        CategoryEntity savedCategory = categoryRepository.save(category);
        log.info("Updated category: {}", savedCategory.getId());

        return CategoryResponse.fromEntity(savedCategory);
    }

    @Transactional
    public void deleteCategory(UUID categoryId, UUID userId) {
        CategoryEntity category = findCategoryAndValidateOwnership(categoryId, userId);
        categoryRepository.delete(category);
        log.info("Deleted category: {}", categoryId);
    }

    private CategoryEntity findCategoryAndValidateOwnership(UUID categoryId, UUID userId) {
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        if (!category.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to access this category");
        }

        return category;
    }

    @Transactional(readOnly = true)
    public boolean categoryExistsForUser(UUID categoryId, UUID userId) {
        return categoryRepository.existsByIdAndUserId(categoryId, userId);
    }
}
