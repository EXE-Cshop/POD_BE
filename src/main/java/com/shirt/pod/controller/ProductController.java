package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.CreateProductRequest;
import com.shirt.pod.model.dto.request.CreateProductVariantRequest;
import com.shirt.pod.model.dto.request.UpdateProductRequest;
import com.shirt.pod.model.dto.request.UpdateProductVariantRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.ProductDetailDTO;
import com.shirt.pod.model.dto.response.ProductDTO;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import com.shirt.pod.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import com.shirt.pod.security.SecurityConstants;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "APIs for managing products")
public class ProductController {

        private final ProductService productService;

        @Operation(summary = "Get products with pagination, search, and category filter")
        @GetMapping
        public ApiResponse<Page<ProductDTO>> getProducts(
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size,
                        @RequestParam(defaultValue = "id,desc") String sort) {
                
                String[] sortParams = sort.split(",");
                Sort.Direction dir = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                String property = sortParams[0];
                Pageable pageable = PageRequest.of(page, size, Sort.by(dir, property));
                
                Page<ProductDTO> products = productService.getProducts(categoryId, keyword, pageable);

                return ApiResponse.<Page<ProductDTO>>builder()
                                .code(HttpStatus.OK.value())
                                .message("Get products successfully")
                                .data(products)
                                .build();
        }

        @Operation(summary = "Get all products (list, optional filter by active status)")
        @GetMapping("/all")
        public ApiResponse<List<ProductDTO>> getAllProducts(
                        @RequestParam(required = false) Boolean activeOnly) {
                List<ProductDTO> products = productService.getAllProducts(activeOnly);

                return ApiResponse.<List<ProductDTO>>builder()
                                .code(HttpStatus.OK.value())
                                .message("Get all products successfully")
                                .data(products)
                                .build();
        }

        @Operation(summary = "Get product by ID", description = "Retrieve product details by its unique identifier")
        @GetMapping("/{id}")
        public ApiResponse<ProductDTO> getProductById(
                        @PathVariable Long id) {
                ProductDTO product = productService.getProductById(id);

                return ApiResponse.<ProductDTO>builder()
                                .code(HttpStatus.OK.value())
                                .message("Get product successfully")
                                .data(product)
                                .build();
        }

        @Operation(summary = "Get product detail by ID", description = "Retrieve full product details including variants and images")
        @GetMapping("/{id}/detail")
        public ApiResponse<ProductDetailDTO> getProductDetail(
                        @PathVariable Long id) {
                ProductDetailDTO productDetail = productService.getProductDetailById(id);

                return ApiResponse.<ProductDetailDTO>builder()
                                .code(HttpStatus.OK.value())
                                .message("Get product detail successfully")
                                .data(productDetail)
                                .build();
        }

        @Operation(summary = "Get product detail by slug", description = "Retrieve full product details by its SEO-friendly slug")
        @GetMapping("/slug/{slug}")
        public ApiResponse<ProductDetailDTO> getProductDetailBySlug(
                        @PathVariable String slug) {
                ProductDetailDTO productDetail = productService.getProductDetailBySlug(slug);

                return ApiResponse.<ProductDetailDTO>builder()
                                .code(HttpStatus.OK.value())
                                .message("Get product detail by slug successfully")
                                .data(productDetail)
                                .build();
        }

        @Operation(summary = "Get trending products")
        @GetMapping("/trending")
        public ApiResponse<List<ProductDTO>> getTrendingProducts() {
                List<ProductDTO> products = productService.getTrendingProducts();
                return ApiResponse.<List<ProductDTO>>builder()
                                .code(HttpStatus.OK.value())
                                .message("Get trending products successfully")
                                .data(products)
                                .build();
        }

        @Operation(summary = "Get featured products")
        @GetMapping("/featured")
        public ApiResponse<List<ProductDTO>> getFeaturedProducts() {
                List<ProductDTO> products = productService.getFeaturedProducts();
                return ApiResponse.<List<ProductDTO>>builder()
                                .code(HttpStatus.OK.value())
                                .message("Get featured products successfully")
                                .data(products)
                                .build();
        }

        @Operation(summary = "Create product", description = "Create a new product")
        @PostMapping
        @PreAuthorize("hasRole('ADMIN') or hasAuthority('" + SecurityConstants.PRODUCT_CREATE + "')")
        public ApiResponse<ProductDTO> createProduct(
                        @Valid @RequestBody CreateProductRequest request) {
                ProductDTO product = productService.createProduct(request);

                return ApiResponse.<ProductDTO>builder()
                                .code(HttpStatus.CREATED.value())
                                .message("Product created successfully")
                                .data(product)
                                .build();
        }

        @Operation(summary = "Update product", description = "Update an existing product")
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN') or hasAuthority('" + SecurityConstants.PRODUCT_UPDATE + "')")
        public ApiResponse<ProductDTO> updateProduct(
                        @PathVariable Long id,
                        @Valid @RequestBody UpdateProductRequest request) {
                ProductDTO product = productService.updateProduct(id, request);

                return ApiResponse.<ProductDTO>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product updated successfully")
                                .data(product)
                                .build();
        }

        @Operation(summary = "Delete product", description = "Soft delete a product")
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN') or hasAuthority('" + SecurityConstants.PRODUCT_DELETE + "')")
        public ApiResponse<Void> deleteProduct(
                        @PathVariable Long id) {
                productService.deleteProduct(id);

                return ApiResponse.<Void>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product deleted successfully")
                                .build();
        }

        @Operation(summary = "Activate product", description = "Activate a product")
        @PatchMapping("/{id}/activate")
        @PreAuthorize("hasRole('ADMIN') or hasAuthority('" + SecurityConstants.PRODUCT_UPDATE + "')")
        public ApiResponse<Void> activateProduct(
                        @PathVariable Long id) {
                productService.activateProduct(id);

                return ApiResponse.<Void>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product activated successfully")
                                .build();
        }

        @Operation(summary = "Deactivate product", description = "Deactivate a product")
        @PatchMapping("/{id}/deactivate")
        @PreAuthorize("hasRole('ADMIN') or hasAuthority('" + SecurityConstants.PRODUCT_UPDATE + "')")
        public ApiResponse<Void> deactivateProduct(
                        @PathVariable Long id) {
                productService.deactivateProduct(id);

                return ApiResponse.<Void>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product deactivated successfully")
                                .build();
        }

        @Operation(summary = "Get product variants", description = "Retrieve all variants for a specific product")
        @GetMapping("/{productId}/variants")
        public ApiResponse<List<ProductVariantDTO>> getVariantsByProductId(
                        @PathVariable Long productId) {
                List<ProductVariantDTO> variants = productService.getVariantsByProductId(productId);

                return ApiResponse.<List<ProductVariantDTO>>builder()
                                .code(HttpStatus.OK.value())
                                .message("Get product variants successfully")
                                .data(variants)
                                .build();
        }

        @Operation(summary = "Create product variant", description = "Create a new variant for a product")
        @PostMapping("/{productId}/variants")
        @PreAuthorize("hasRole('ADMIN') or hasAuthority('" + SecurityConstants.VARIANT_CREATE + "')")
        public ApiResponse<ProductVariantDTO> createVariant(
                        @PathVariable Long productId,
                        @Valid @RequestBody CreateProductVariantRequest request) {
                ProductVariantDTO variant = productService.createVariant(productId, request);

                return ApiResponse.<ProductVariantDTO>builder()
                                .code(HttpStatus.CREATED.value())
                                .message("Product variant created successfully")
                                .data(variant)
                                .build();
        }

        @Operation(summary = "Update product variant", description = "Update an existing product variant")
        @PutMapping("/variants/{variantId}")
        @PreAuthorize("hasRole('ADMIN') or hasAuthority('" + SecurityConstants.VARIANT_UPDATE + "')")
        public ApiResponse<ProductVariantDTO> updateVariant(
                        @PathVariable Long variantId,
                        @Valid @RequestBody UpdateProductVariantRequest request) {
                ProductVariantDTO variant = productService.updateVariant(variantId, request);

                return ApiResponse.<ProductVariantDTO>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product variant updated successfully")
                                .data(variant)
                                .build();
        }

        @Operation(summary = "Delete product variant", description = "Soft delete a product variant")
        @DeleteMapping("/variants/{variantId}")
        @PreAuthorize("hasRole('ADMIN') or hasAuthority('" + SecurityConstants.VARIANT_DELETE + "')")
        public ApiResponse<Void> deleteVariant(
                        @PathVariable Long variantId) {
                productService.deleteVariant(variantId);

                return ApiResponse.<Void>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product variant deleted successfully")
                                .build();
        }
}
