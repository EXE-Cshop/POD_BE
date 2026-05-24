package com.shirt.pod.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePromotionRequest {
    @NotBlank(message = "Promotion code is required")
    private String code;
    private String description;
    @NotBlank(message = "Discount type is required")
    private String discountType; // PERCENTAGE or FIXED_AMOUNT
    @NotNull(message = "Discount value is required")
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private Integer maxUsageCount;
    private Instant startDate;
    private Instant endDate;
}
