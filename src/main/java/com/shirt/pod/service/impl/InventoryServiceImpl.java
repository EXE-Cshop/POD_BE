package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.mapper.InventoryMapper;
import com.shirt.pod.model.dto.request.BulkUpdateStockRequest;
import com.shirt.pod.model.dto.request.UpdateStockRequest;
import com.shirt.pod.model.dto.response.InventorySummaryDTO;
import com.shirt.pod.model.dto.response.ProductInventoryDTO;
import com.shirt.pod.model.dto.response.StockAvailabilityDTO;
import com.shirt.pod.model.dto.response.VariantInventoryDTO;
import com.shirt.pod.model.entity.Product;
import com.shirt.pod.model.entity.ProductVariant;
import com.shirt.pod.model.entity.enums.StockStatus;
import com.shirt.pod.repository.ProductRepository;
import com.shirt.pod.repository.ProductVariantRepository;
import com.shirt.pod.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 10;

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(readOnly = true)
    public InventorySummaryDTO getInventorySummary() {
        log.debug("Getting inventory summary");

        long totalProducts = productRepository.count();
        long totalVariants = productVariantRepository.count();
        long outOfStockVariants = productVariantRepository.countOutOfStock();
        long lowStockVariants = productVariantRepository.countLowStock(DEFAULT_LOW_STOCK_THRESHOLD);
        long inStockVariants = totalVariants - outOfStockVariants - lowStockVariants;

        InventorySummaryDTO summary = InventorySummaryDTO.builder()
                .totalProducts(totalProducts)
                .totalVariants(totalVariants)
                .inStockVariants(inStockVariants)
                .outOfStockVariants(outOfStockVariants)
                .lowStockVariants(lowStockVariants)
                .build();

        log.info("Inventory summary: {} products, {} variants ({} in stock, {} out of stock, {} low stock)",
                totalProducts, totalVariants, inStockVariants, outOfStockVariants, lowStockVariants);
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public InventorySummaryDTO getInventorySummaryByProduct(Long productId) {
        log.debug("Getting inventory summary for product id: {}", productId);

        if (!productRepository.existsById(productId)) {
            log.warn("Product not found with id: {}", productId);
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", productId);
        }

        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        long totalVariants = variants.size();
        long outOfStockVariants = variants.stream()
                .filter(v -> v.getStockQuantity() == null || v.getStockQuantity() == 0)
                .count();
        long lowStockVariants = variants.stream()
                .filter(v -> {
                    Integer stock = v.getStockQuantity();
                    return stock != null && stock > 0 && stock <= DEFAULT_LOW_STOCK_THRESHOLD;
                })
                .count();
        long inStockVariants = totalVariants - outOfStockVariants - lowStockVariants;

        InventorySummaryDTO summary = InventorySummaryDTO.builder()
                .totalProducts(1L)
                .totalVariants(totalVariants)
                .inStockVariants(inStockVariants)
                .outOfStockVariants(outOfStockVariants)
                .lowStockVariants(lowStockVariants)
                .build();

        log.info("Inventory summary for product {}: {} variants ({} in stock, {} out of stock, {} low stock)",
                productId, totalVariants, inStockVariants, outOfStockVariants, lowStockVariants);
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductInventoryDTO> getProductInventory(
            StockStatus status,
            String productName,
            Integer lowStockThreshold,
            Pageable pageable) {
        log.debug("Getting product inventory with filters: status={}, productName={}, threshold={}",
                status, productName, lowStockThreshold);

        int threshold = lowStockThreshold != null ? lowStockThreshold : DEFAULT_LOW_STOCK_THRESHOLD;

        List<Product> products;
        if (productName != null && !productName.trim().isEmpty()) {
            products = productRepository.findAll().stream()
                    .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(productName.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            products = productRepository.findAll();
        }

        List<ProductInventoryDTO> productInventories = products.stream()
                .map(product -> calculateProductInventory(product, threshold))
                .filter(dto -> {
                    if (status == null) {
                        return true;
                    }
                    return dto.getStockStatus() == status;
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), productInventories.size());
        List<ProductInventoryDTO> pagedList = start < productInventories.size()
                ? productInventories.subList(start, end)
                : List.of();

        log.info("Found {} products matching filters", productInventories.size());
        return new PageImpl<>(pagedList, pageable, productInventories.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VariantInventoryDTO> getVariantInventory(
            Long productId,
            StockStatus status,
            String sku,
            Integer lowStockThreshold,
            Pageable pageable) {
        log.debug("Getting variant inventory with filters: productId={}, status={}, sku={}, threshold={}",
                productId, status, sku, lowStockThreshold);

        int threshold = lowStockThreshold != null ? lowStockThreshold : DEFAULT_LOW_STOCK_THRESHOLD;

        List<ProductVariant> variants;
        if (productId != null) {
            if (!productRepository.existsById(productId)) {
                log.warn("Product not found with id: {}", productId);
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", productId);
            }
            variants = productVariantRepository.findByProductId(productId);
        } else {
            variants = productVariantRepository.findAll();
        }

        if (sku != null && !sku.trim().isEmpty()) {
            variants = variants.stream()
                    .filter(v -> v.getSku() != null && v.getSku().toLowerCase().contains(sku.toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<VariantInventoryDTO> variantInventories = variants.stream()
                .map(variant -> {
                    VariantInventoryDTO dto = inventoryMapper.toVariantInventoryDTO(variant);
                    StockStatus stockStatus = inventoryMapper.calculateStockStatus(
                            variant.getStockQuantity(), threshold);
                    dto.setStockStatus(stockStatus);
                    dto.setLowStockThreshold(threshold);
                    return dto;
                })
                .filter(dto -> {
                    if (status == null) {
                        return true;
                    }
                    return dto.getStockStatus() == status;
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), variantInventories.size());
        List<VariantInventoryDTO> pagedList = start < variantInventories.size()
                ? variantInventories.subList(start, end)
                : List.of();

        log.info("Found {} variants matching filters", variantInventories.size());
        return new PageImpl<>(pagedList, pageable, variantInventories.size());
    }

    @Override
    @Transactional
    public VariantInventoryDTO updateStock(Long variantId, UpdateStockRequest request) {
        log.debug("Updating stock for variant id: {}, quantity change: {}", variantId, request.getQuantityChange());

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> {
                    log.warn("Variant not found with id: {}", variantId);
                    return new AppException(ErrorCode.VARIANT_NOT_FOUND, "id", variantId);
                });

        Integer currentStock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;
        Integer newStock = currentStock + request.getQuantityChange();

        if (newStock < 0) {
            log.warn("Stock update would result in negative stock. Current: {}, Change: {}", currentStock, request.getQuantityChange());
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK, variant.getSku(), currentStock, Math.abs(request.getQuantityChange()));
        }

        variant.setStockQuantity(newStock);
        ProductVariant updatedVariant = productVariantRepository.save(variant);

        VariantInventoryDTO dto = inventoryMapper.toVariantInventoryDTO(updatedVariant);
        StockStatus stockStatus = inventoryMapper.calculateStockStatus(newStock, DEFAULT_LOW_STOCK_THRESHOLD);
        dto.setStockStatus(stockStatus);
        dto.setLowStockThreshold(DEFAULT_LOW_STOCK_THRESHOLD);

        log.info("Updated stock for variant {}: {} -> {} (reason: {})",
                variantId, currentStock, newStock, request.getReason());
        return dto;
    }

    @Override
    @Transactional
    public List<VariantInventoryDTO> bulkUpdateStock(BulkUpdateStockRequest request) {
        log.debug("Bulk updating stock for {} variants", request.getUpdates().size());

        return request.getUpdates().stream()
                .map(item -> {
                    UpdateStockRequest updateRequest = UpdateStockRequest.builder()
                            .quantityChange(item.getQuantityChange())
                            .reason(item.getReason())
                            .notes(item.getNotes())
                            .build();
                    return updateStock(item.getVariantId(), updateRequest);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StockAvailabilityDTO checkStockAvailability(Long variantId, Integer quantity) {
        log.debug("Checking stock availability for variant id: {}, quantity: {}", variantId, quantity);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> {
                    log.warn("Variant not found with id: {}", variantId);
                    return new AppException(ErrorCode.VARIANT_NOT_FOUND, "id", variantId);
                });

        boolean available = Boolean.TRUE.equals(variant.getActive());
        Integer currentStock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;
        boolean sufficient = available && currentStock >= quantity;

        StockAvailabilityDTO dto = StockAvailabilityDTO.builder()
                .available(available)
                .currentStock(currentStock)
                .requestedQuantity(quantity)
                .sufficient(sufficient)
                .build();

        log.info("Stock availability check for variant {}: available={}, currentStock={}, requested={}, sufficient={}",
                variantId, available, currentStock, quantity, sufficient);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableStock(Long variantId) {
        log.debug("Getting available stock for variant id: {}", variantId);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> {
                    log.warn("Variant not found with id: {}", variantId);
                    return new AppException(ErrorCode.VARIANT_NOT_FOUND, "id", variantId);
                });

        if (!Boolean.TRUE.equals(variant.getActive())) {
            return 0;
        }

        Integer stock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;
        log.debug("Available stock for variant {}: {}", variantId, stock);
        return stock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantInventoryDTO> getLowStockVariants(Integer threshold) {
        log.debug("Getting low stock variants with threshold: {}", threshold);

        int thresholdValue = threshold != null ? threshold : DEFAULT_LOW_STOCK_THRESHOLD;
        List<ProductVariant> lowStockVariants = productVariantRepository.findLowStockVariants(thresholdValue);

        List<VariantInventoryDTO> dtos = lowStockVariants.stream()
                .map(variant -> {
                    VariantInventoryDTO dto = inventoryMapper.toVariantInventoryDTO(variant);
                    dto.setStockStatus(StockStatus.LOW_STOCK);
                    dto.setLowStockThreshold(thresholdValue);
                    return dto;
                })
                .collect(Collectors.toList());

        log.info("Found {} variants with low stock", dtos.size());
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInventoryDTO> getProductsWithLowStock(Integer threshold) {
        log.debug("Getting products with low stock variants, threshold: {}", threshold);

        int thresholdValue = threshold != null ? threshold : DEFAULT_LOW_STOCK_THRESHOLD;
        List<ProductVariant> lowStockVariants = productVariantRepository.findLowStockVariants(thresholdValue);
        
        List<Product> products = lowStockVariants.stream()
                .map(ProductVariant::getProduct)
                .distinct()
                .collect(Collectors.toList());

        List<ProductInventoryDTO> dtos = products.stream()
                .map(product -> calculateProductInventory(product, thresholdValue))
                .collect(Collectors.toList());

        log.info("Found {} products with low stock variants", dtos.size());
        return dtos;
    }

    private ProductInventoryDTO calculateProductInventory(Product product, int threshold) {
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());

        int totalVariants = variants.size();
        int inStockVariants = 0;
        int outOfStockVariants = 0;
        int lowStockVariants = 0;
        int totalStockQuantity = 0;

        for (ProductVariant variant : variants) {
            Integer stock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;
            totalStockQuantity += stock;

            if (stock == 0) {
                outOfStockVariants++;
            } else if (stock > 0 && stock <= threshold) {
                lowStockVariants++;
            } else {
                inStockVariants++;
            }
        }

        StockStatus stockStatus = null;
        if (outOfStockVariants == totalVariants) {
            stockStatus = StockStatus.OUT_OF_STOCK;
        } else if (lowStockVariants > 0 || outOfStockVariants > 0) {
            if (inStockVariants == 0) {
                stockStatus = StockStatus.LOW_STOCK;
            }
        } else {
            stockStatus = StockStatus.IN_STOCK;
        }

        ProductInventoryDTO dto = inventoryMapper.toProductInventoryDTO(product);
        dto.setTotalVariants(totalVariants);
        dto.setInStockVariants(inStockVariants);
        dto.setOutOfStockVariants(outOfStockVariants);
        dto.setLowStockThreshold(threshold);
        dto.setLowStockVariants(lowStockVariants);
        dto.setTotalStockQuantity(totalStockQuantity);
        dto.setStockStatus(stockStatus);

        return dto;
    }
}
