package com.shirt.pod.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductDetailDTO {
    Long id;
    String name;
    String slug;
    String description;
    BigDecimal basePrice;
    String material;
    String imageUrl;
    Boolean active;
    Boolean isTrending;
    Boolean isFeatured;
    String tags;
    CategoryDTO category;
    Instant createdDate;
    Instant modifiedDate;

    List<ProductVariantDTO> variants;
    List<ProductImageDTO> images;
    List<ReviewDTO> reviews;
}
