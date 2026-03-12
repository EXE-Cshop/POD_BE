package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.ProductVariantCreateRequest;
import com.shirt.pod.model.dto.request.ProductVariantFilterRequest;
import com.shirt.pod.model.dto.request.ProductVariantUpdateRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import com.shirt.pod.service.ProductVariantService;
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
@RequestMapping("/api/v1/product-variants")
@RequiredArgsConstructor
@Tag(name = "Product Variant", description = "APIs for managing product variants (color, size, SKU)")
public class ProductVariantController {

        private final ProductVariantService productVariantService;

        @Operation(summary = "Get list of product variants", description = "Retrieve a paginated list of product variants with optional filters. Use request body to pass filter parameters.")
        @GetMapping
        @PreAuthorize("hasAuthority('" + SecurityConstants.VARIANT_VIEW + "')")
        public ApiResponse<Page<ProductVariantDTO>> getAll(@ModelAttribute ProductVariantFilterRequest filterRequest) {
                return ApiResponse.<Page<ProductVariantDTO>>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product variants fetched successfully")
                                .data(productVariantService.getAll(filterRequest))
                                .build();
        }

        @Operation(summary = "Get product variant by ID", description = "Retrieve a single product variant by its ID")
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('" + SecurityConstants.VARIANT_VIEW + "')")
        public ApiResponse<ProductVariantDTO> getById(@PathVariable Long id) {
                return ApiResponse.<ProductVariantDTO>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product variant fetched successfully")
                                .data(productVariantService.getById(id))
                                .build();
        }

        @Operation(summary = "Create new product variant", description = "Create a new product variant for a base product")
        @PostMapping
        @PreAuthorize("hasAuthority('" + SecurityConstants.VARIANT_CREATE + "')")
        public ApiResponse<ProductVariantDTO> create(@Valid @RequestBody ProductVariantCreateRequest request) {
                return ApiResponse.<ProductVariantDTO>builder()
                                .code(HttpStatus.CREATED.value())
                                .message("Product variant created successfully")
                                .data(productVariantService.create(request))
                                .build();
        }

        @Operation(summary = "Update product variant", description = "Update an existing product variant. Supports partial updates - only provided fields will be updated")
        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('" + SecurityConstants.VARIANT_UPDATE + "')")
        public ApiResponse<ProductVariantDTO> update(
                        @PathVariable Long id,
                        @Valid @RequestBody ProductVariantUpdateRequest request) {
                return ApiResponse.<ProductVariantDTO>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product variant updated successfully")
                                .data(productVariantService.update(id, request))
                                .build();
        }

        @Operation(summary = "Delete product variant", description = "Soft delete a product variant by setting active = false")
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('" + SecurityConstants.VARIANT_DELETE + "')")
        public ApiResponse<Void> delete(@PathVariable Long id) {
                productVariantService.delete(id);
                return ApiResponse.<Void>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product variant deleted successfully")
                                .build();
        }
}
