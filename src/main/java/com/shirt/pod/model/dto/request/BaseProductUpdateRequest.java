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

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for partial update of BaseProduct
 * All fields are optional - only provided fields will be updated
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BaseProductUpdateRequest implements Serializable {

    @Size(max = 255, message = "Product name must not exceed 255 characters")
    String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    String description;

    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    BigDecimal basePrice;

    @Size(max = 100, message = "Material must not exceed 100 characters")
    String material;

    @Size(max = 100, message = "Print technology must not exceed 100 characters")
    String printTechnology;

    @Size(max = 1000, message = "Image URL must not exceed 1000 characters")
    String imageUrl;

    Boolean active;
}
