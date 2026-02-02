package com.shirt.pod.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class CreatePrintAreaRequest implements Serializable {

    @Size(max = 50, message = "Print area name must not exceed 50 characters")
    @Builder.Default
    String name = "Front";

    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.01", message = "Width must be greater than 0")
    BigDecimal widthMm;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.01", message = "Height must be greater than 0")
    BigDecimal heightMm;

    @Builder.Default
    @DecimalMin(value = "0.0", message = "Top offset must be greater than or equal to 0")
    BigDecimal topOffsetMm = BigDecimal.ZERO;

    @Builder.Default
    @DecimalMin(value = "0.0", message = "Left offset must be greater than or equal to 0")
    BigDecimal leftOffsetMm = BigDecimal.ZERO;

    String maskImageUrl;
}
