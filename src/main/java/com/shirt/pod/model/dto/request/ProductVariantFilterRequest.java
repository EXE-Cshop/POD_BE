package com.shirt.pod.model.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductVariantFilterRequest {

    // Filter fields
    Long baseProductId;
    String colorName;
    String size; // Product size: S, M, L, XL
    String sku;
    Boolean active;

    // Pagination fields
    @Builder.Default
    Integer page = 1;

    @Builder.Default
    Integer pageSize = 10; // Page size for pagination

    @Builder.Default
    String sortBy = "createdDate";

    @Builder.Default
    String order = "DESC";
}
