package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.BulkUpdateStockRequest;
import com.shirt.pod.model.dto.request.UpdateStockRequest;
import com.shirt.pod.model.dto.response.InventorySummaryDTO;
import com.shirt.pod.model.dto.response.ProductInventoryDTO;
import com.shirt.pod.model.dto.response.StockAvailabilityDTO;
import com.shirt.pod.model.dto.response.VariantInventoryDTO;
import com.shirt.pod.model.entity.enums.StockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {

    // ========== Summary ==========

    /**
     * Get inventory summary for all products
     */
    InventorySummaryDTO getInventorySummary();

    /**
     * Get inventory summary for a specific product
     */
    InventorySummaryDTO getInventorySummaryByProduct(Long productId);

    // ========== Product Inventory ==========

    /**
     * Get paginated list of products with inventory information
     */
    Page<ProductInventoryDTO> getProductInventory(
            StockStatus status,
            String productName,
            Integer lowStockThreshold,
            Pageable pageable
    );

    // ========== Variant Inventory ==========

    /**
     * Get paginated list of variants with inventory information
     */
    Page<VariantInventoryDTO> getVariantInventory(
            Long productId,
            StockStatus status,
            String sku,
            Integer lowStockThreshold,
            Pageable pageable
    );

    // ========== Stock Update ==========

    /**
     * Update stock quantity for a variant
     */
    VariantInventoryDTO updateStock(Long variantId, UpdateStockRequest request);

    /**
     * Bulk update stock for multiple variants
     */
    List<VariantInventoryDTO> bulkUpdateStock(BulkUpdateStockRequest request);

    // ========== Stock Availability Check ==========

    /**
     * Check if variant has sufficient stock for requested quantity
     */
    StockAvailabilityDTO checkStockAvailability(Long variantId, Integer quantity);

    /**
     * Get available stock quantity for a variant
     */
    Integer getAvailableStock(Long variantId);

    // ========== Low Stock Alerts ==========

    /**
     * Get list of variants with low stock
     */
    List<VariantInventoryDTO> getLowStockVariants(Integer threshold);

    /**
     * Get list of products with low stock variants
     */
    List<ProductInventoryDTO> getProductsWithLowStock(Integer threshold);
}
