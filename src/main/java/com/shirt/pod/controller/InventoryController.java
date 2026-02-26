package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.BulkUpdateStockRequest;
import com.shirt.pod.model.dto.request.UpdateStockRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.InventorySummaryDTO;
import com.shirt.pod.model.dto.response.ProductInventoryDTO;
import com.shirt.pod.model.dto.response.StockAvailabilityDTO;
import com.shirt.pod.model.dto.response.VariantInventoryDTO;
import com.shirt.pod.model.entity.enums.StockStatus;
import com.shirt.pod.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/summary")
    public ApiResponse<InventorySummaryDTO> getInventorySummary() {
        InventorySummaryDTO summary = inventoryService.getInventorySummary();

        return ApiResponse.<InventorySummaryDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Get inventory summary successfully")
                .data(summary)
                .build();
    }

    @GetMapping("/summary/products/{productId}")
    public ApiResponse<InventorySummaryDTO> getInventorySummaryByProduct(
            @PathVariable Long productId) {
        InventorySummaryDTO summary = inventoryService.getInventorySummaryByProduct(productId);

        return ApiResponse.<InventorySummaryDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Get inventory summary for product successfully")
                .data(summary)
                .build();
    }

    @GetMapping("/products")
    public ApiResponse<Page<ProductInventoryDTO>> getProductInventory(
            @RequestParam(required = false) StockStatus status,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false, defaultValue = "10") Integer lowStockThreshold,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "productName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductInventoryDTO> productInventory = inventoryService.getProductInventory(
                status, productName, lowStockThreshold, pageable);

        return ApiResponse.<Page<ProductInventoryDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Get product inventory successfully")
                .data(productInventory)
                .build();
    }

    @GetMapping("/variants")
    public ApiResponse<Page<VariantInventoryDTO>> getVariantInventory(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) StockStatus status,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false, defaultValue = "10") Integer lowStockThreshold,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "sku") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VariantInventoryDTO> variantInventory = inventoryService.getVariantInventory(
                productId, status, sku, lowStockThreshold, pageable);

        return ApiResponse.<Page<VariantInventoryDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Get variant inventory successfully")
                .data(variantInventory)
                .build();
    }

    @PutMapping("/variants/{variantId}/stock")
    public ApiResponse<VariantInventoryDTO> updateStock(
            @PathVariable Long variantId,
            @Valid @RequestBody UpdateStockRequest request) {
        VariantInventoryDTO updatedVariant = inventoryService.updateStock(variantId, request);

        return ApiResponse.<VariantInventoryDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Stock updated successfully")
                .data(updatedVariant)
                .build();
    }

    @PutMapping("/variants/bulk-stock")
    public ApiResponse<List<VariantInventoryDTO>> bulkUpdateStock(
            @Valid @RequestBody BulkUpdateStockRequest request) {
        List<VariantInventoryDTO> updatedVariants = inventoryService.bulkUpdateStock(request);

        return ApiResponse.<List<VariantInventoryDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Bulk stock update completed successfully")
                .data(updatedVariants)
                .build();
    }

    @GetMapping("/variants/{variantId}/availability")
    public ApiResponse<StockAvailabilityDTO> checkStockAvailability(
            @PathVariable Long variantId,
            @RequestParam Integer quantity) {
        StockAvailabilityDTO availability = inventoryService.checkStockAvailability(variantId, quantity);

        return ApiResponse.<StockAvailabilityDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Stock availability checked successfully")
                .data(availability)
                .build();
    }

    @GetMapping("/variants/{variantId}/available-stock")
    public ApiResponse<Integer> getAvailableStock(
            @PathVariable Long variantId) {
        Integer availableStock = inventoryService.getAvailableStock(variantId);

        return ApiResponse.<Integer>builder()
                .code(HttpStatus.OK.value())
                .message("Get available stock successfully")
                .data(availableStock)
                .build();
    }

    @GetMapping("/low-stock")
    public ApiResponse<List<VariantInventoryDTO>> getLowStockVariants(
            @RequestParam(required = false, defaultValue = "10") Integer threshold) {
        List<VariantInventoryDTO> lowStockVariants = inventoryService.getLowStockVariants(threshold);

        return ApiResponse.<List<VariantInventoryDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Get low stock variants successfully")
                .data(lowStockVariants)
                .build();
    }

    @GetMapping("/products/low-stock")
    public ApiResponse<List<ProductInventoryDTO>> getProductsWithLowStock(
            @RequestParam(required = false, defaultValue = "10") Integer threshold) {
        List<ProductInventoryDTO> productsWithLowStock = inventoryService.getProductsWithLowStock(threshold);

        return ApiResponse.<List<ProductInventoryDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Get products with low stock successfully")
                .data(productsWithLowStock)
                .build();
    }
}
