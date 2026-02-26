package com.shirt.pod.model.dto.response;

import com.shirt.pod.model.entity.enums.StockStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class VariantInventoryDTO {

    Long variantId;
    Long baseProductId;
    String baseProductName;
    String sku;
    String colorName;
    String colorHex;
    String size;
    Integer stockQuantity;
    StockStatus stockStatus;       // IN_STOCK, OUT_OF_STOCK, LOW_STOCK
    Integer lowStockThreshold;     // Ngưỡng cảnh báo tồn kho thấp
    Boolean active;
    Instant createdDate;
    String createdBy;
}
