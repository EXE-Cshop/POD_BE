package com.shirt.pod.model.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductVariantDTO implements Serializable {

    Long id;
    Long baseProductId;
    String baseProductName;
    String colorName;
    String colorHex;
    String size;
    String sku;
    Integer stockQuantity;
    String frontImageUrl;
    String backImageUrl;
    BigDecimal priceAdjustment;
    Boolean active;
    Instant createdDate;
    String createdBy;
}
