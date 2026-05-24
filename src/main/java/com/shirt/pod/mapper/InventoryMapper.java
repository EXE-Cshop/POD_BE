package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.response.ProductInventoryDTO;
import com.shirt.pod.model.dto.response.VariantInventoryDTO;
import com.shirt.pod.model.entity.Product;
import com.shirt.pod.model.entity.ProductVariant;
import com.shirt.pod.model.entity.enums.StockStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "variantId", source = "id")
    @Mapping(target = "baseProductId", source = "product.id")
    @Mapping(target = "baseProductName", source = "product.name")
    @Mapping(target = "stockStatus", ignore = true)
    @Mapping(target = "lowStockThreshold", ignore = true)
    VariantInventoryDTO toVariantInventoryDTO(ProductVariant variant);

    List<VariantInventoryDTO> toVariantInventoryDTOList(List<ProductVariant> variants);

    @Mapping(target = "productId", source = "id")
    @Mapping(target = "productName", source = "name")
    @Mapping(target = "totalVariants", ignore = true)
    @Mapping(target = "inStockVariants", ignore = true)
    @Mapping(target = "outOfStockVariants", ignore = true)
    @Mapping(target = "lowStockVariants", ignore = true)
    @Mapping(target = "totalStockQuantity", ignore = true)
    @Mapping(target = "stockStatus", ignore = true)
    ProductInventoryDTO toProductInventoryDTO(Product product);

    List<ProductInventoryDTO> toProductInventoryDTOList(List<Product> products);

    @Named("calculateStockStatus")
    default StockStatus calculateStockStatus(Integer stockQuantity, Integer lowStockThreshold) {
        if (stockQuantity == null || stockQuantity == 0) {
            return StockStatus.OUT_OF_STOCK;
        }
        if (lowStockThreshold != null && stockQuantity > 0 && stockQuantity <= lowStockThreshold) {
            return StockStatus.LOW_STOCK;
        }
        return StockStatus.IN_STOCK;
    }
}
