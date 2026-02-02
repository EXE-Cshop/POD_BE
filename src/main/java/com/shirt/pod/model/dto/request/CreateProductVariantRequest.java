package com.shirt.pod.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreateProductVariantRequest implements Serializable {

    @Size(max = 50, message = "Color name must not exceed 50 characters")
    String colorName;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color hex must be a valid hex color code (e.g., #FF5733)")
    @Size(max = 10, message = "Color hex must not exceed 10 characters")
    String colorHex;

    @NotBlank(message = "Size is required")
    @Size(max = 10, message = "Size must not exceed 10 characters")
    String size;

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    String sku;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    Integer stockQuantity;

    String imageUrl;

    @Builder.Default
    @DecimalMin(value = "0.0", message = "Price adjustment must be greater than or equal to 0")
    BigDecimal priceAdjustment = BigDecimal.ZERO;

    @Builder.Default
    Boolean active = true;
}
