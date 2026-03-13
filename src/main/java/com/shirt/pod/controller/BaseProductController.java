package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.BaseProductCreateRequest;
import com.shirt.pod.model.dto.request.BaseProductFilterRequest;
import com.shirt.pod.model.dto.request.BaseProductUpdateRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.BaseProductDTO;
import com.shirt.pod.service.BaseProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.shirt.pod.security.SecurityConstants;

@RestController
@RequestMapping("/api/v1/base-products")
@RequiredArgsConstructor
@Tag(name = "Base Product", description = "APIs for managing base products (product templates)")
public class BaseProductController {

    private final BaseProductService baseProductService;

    @Operation(summary = "Get list of base products", description = "Retrieve a paginated list of base products with optional filters. Use request body to pass filter parameters.")
    @GetMapping
    public ApiResponse<Page<BaseProductDTO>> getAll(@ModelAttribute BaseProductFilterRequest filterRequest) {
        return ApiResponse.<Page<BaseProductDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Base products fetched successfully")
                .data(baseProductService.getAll(filterRequest))
                .build();
    }

    @Operation(summary = "Get base product by ID", description = "Retrieve a single base product by its ID")
    @GetMapping("/{id}")
    public ApiResponse<BaseProductDTO> getById(@PathVariable Long id) {
        return ApiResponse.<BaseProductDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Base product fetched successfully")
                .data(baseProductService.getById(id))
                .build();
    }

    @Operation(summary = "Create new base product", description = "Create a new base product template")
    @PostMapping
    @PreAuthorize("hasAuthority('" + SecurityConstants.BASE_PRODUCT_CREATE + "')")
    public ApiResponse<BaseProductDTO> create(@Valid @RequestBody BaseProductCreateRequest request) {
        return ApiResponse.<BaseProductDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Base product created successfully")
                .data(baseProductService.create(request))
                .build();
    }

    @Operation(summary = "Update base product", description = "Update an existing base product. Supports partial updates - only provided fields will be updated")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + SecurityConstants.BASE_PRODUCT_UPDATE + "')")
    public ApiResponse<BaseProductDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody BaseProductUpdateRequest request) {
        return ApiResponse.<BaseProductDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Base product updated successfully")
                .data(baseProductService.update(id, request))
                .build();
    }

    @Operation(summary = "Delete base product", description = "Soft delete a base product by setting active = false")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + SecurityConstants.BASE_PRODUCT_DELETE + "')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        baseProductService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Base product deleted successfully")
                .build();
    }
}
