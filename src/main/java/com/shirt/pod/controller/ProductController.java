package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.CreatePrintAreaRequest;
import com.shirt.pod.model.dto.request.CreateProductRequest;
import com.shirt.pod.model.dto.request.CreateProductVariantRequest;
import com.shirt.pod.model.dto.request.UpdatePrintAreaRequest;
import com.shirt.pod.model.dto.request.UpdateProductRequest;
import com.shirt.pod.model.dto.request.UpdateProductVariantRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.PrintAreaDTO;
import com.shirt.pod.model.dto.response.ProductDetailDTO;
import com.shirt.pod.model.dto.response.ProductDTO;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import com.shirt.pod.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "APIs for managing products")
public class ProductController {

        private final ProductService productService;

        @Operation(summary = "Get all products", description = "Retrieve a list of products, optionally filtered by active status")
        @GetMapping
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

        @Operation(summary = "Get product detail", description = "Retrieve full product details including variants and print areas")
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

        @Operation(summary = "Create product", description = "Create a new product")
        @PostMapping
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
        public ApiResponse<Void> deleteVariant(
                        @PathVariable Long variantId) {
                productService.deleteVariant(variantId);

                return ApiResponse.<Void>builder()
                                .code(HttpStatus.OK.value())
                                .message("Product variant deleted successfully")
                                .build();
        }

        @Operation(summary = "Get product print areas", description = "Retrieve all print areas for a specific product")
        @GetMapping("/{productId}/print-areas")
        public ApiResponse<List<PrintAreaDTO>> getPrintAreasByProductId(
                        @PathVariable Long productId) {
                List<PrintAreaDTO> printAreas = productService.getPrintAreasByProductId(productId);

                return ApiResponse.<List<PrintAreaDTO>>builder()
                                .code(HttpStatus.OK.value())
                                .message("Get product print areas successfully")
                                .data(printAreas)
                                .build();
        }

        @Operation(summary = "Create print area", description = "Create a new print area for a product")
        @PostMapping("/{productId}/print-areas")
        public ApiResponse<PrintAreaDTO> createPrintArea(
                        @PathVariable Long productId,
                        @Valid @RequestBody CreatePrintAreaRequest request) {
                PrintAreaDTO printArea = productService.createPrintArea(productId, request);

                return ApiResponse.<PrintAreaDTO>builder()
                                .code(HttpStatus.CREATED.value())
                                .message("Print area created successfully")
                                .data(printArea)
                                .build();
        }

        @Operation(summary = "Update print area", description = "Update an existing print area")
        @PutMapping("/print-areas/{printAreaId}")
        public ApiResponse<PrintAreaDTO> updatePrintArea(
                        @PathVariable Long printAreaId,
                        @Valid @RequestBody UpdatePrintAreaRequest request) {
                PrintAreaDTO printArea = productService.updatePrintArea(printAreaId, request);

                return ApiResponse.<PrintAreaDTO>builder()
                                .code(HttpStatus.OK.value())
                                .message("Print area updated successfully")
                                .data(printArea)
                                .build();
        }

        @Operation(summary = "Delete print area", description = "Soft delete a print area")
        @DeleteMapping("/print-areas/{printAreaId}")
        public ApiResponse<Void> deletePrintArea(
                        @PathVariable Long printAreaId) {
                productService.deletePrintArea(printAreaId);

                return ApiResponse.<Void>builder()
                                .code(HttpStatus.OK.value())
                                .message("Print area deleted successfully")
                                .build();
        }
}
