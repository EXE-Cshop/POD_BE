package com.shirt.pod.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UpdateProductRequest {

    @Size(max = 255, message = "Product name must not exceed 255 characters")
    String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description;

    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    BigDecimal basePrice;

    @Size(max = 100, message = "Material must not exceed 100 characters")
    String material;

    Long categoryId;

    String imageUrl;

    Boolean isTrending;

    Boolean isFeatured;

    String tags;

    Boolean active;
}
