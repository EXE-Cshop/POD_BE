package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.CreateCategoryRequest;
import com.shirt.pod.model.dto.request.UpdateCategoryRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.CategoryDTO;
import com.shirt.pod.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all active categories")
    public ApiResponse<List<CategoryDTO>> getAllActiveCategories() {
        List<CategoryDTO> categories = categoryService.getAllActiveCategories();
        return ApiResponse.<List<CategoryDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Categories retrieved successfully")
                .data(categories)
                .build();
    }

    @GetMapping("/all")
    @Operation(summary = "Get all categories (including inactive ones, for admin)")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CATEGORY_VIEW')")
    public ApiResponse<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ApiResponse.<List<CategoryDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("All categories retrieved successfully")
                .data(categories)
                .build();
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get category by slug")
    public ApiResponse<CategoryDTO> getCategoryBySlug(@PathVariable String slug) {
        CategoryDTO category = categoryService.getBySlug(slug);
        return ApiResponse.<CategoryDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Category retrieved successfully")
                .data(category)
                .build();
    }

    @PostMapping
    @Operation(summary = "Create a new category (Admin)")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CATEGORY_CREATE')")
    public ApiResponse<CategoryDTO> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDTO category = categoryService.create(request);
        return ApiResponse.<CategoryDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Category created successfully")
                .data(category)
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category (Admin)")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CATEGORY_UPDATE')")
    public ApiResponse<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryDTO category = categoryService.update(id, request);
        return ApiResponse.<CategoryDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Category updated successfully")
                .data(category)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category (Admin)")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CATEGORY_DELETE')")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Category deleted successfully")
                .build();
    }
}
