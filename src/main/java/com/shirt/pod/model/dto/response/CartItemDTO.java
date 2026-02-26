package com.shirt.pod.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long id;
    private Long productVariantId;
    private String productName;
    private String colorName;
    private String size;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
