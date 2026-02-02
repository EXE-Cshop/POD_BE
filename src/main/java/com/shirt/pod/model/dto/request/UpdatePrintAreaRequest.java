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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UpdatePrintAreaRequest implements Serializable {

    @Size(max = 50, message = "Print area name must not exceed 50 characters")
    String name;

    @DecimalMin(value = "0.01", message = "Width must be greater than 0")
    BigDecimal widthMm;

    @DecimalMin(value = "0.01", message = "Height must be greater than 0")
    BigDecimal heightMm;

    @DecimalMin(value = "0.0", message = "Top offset must be greater than or equal to 0")
    BigDecimal topOffsetMm;

    @DecimalMin(value = "0.0", message = "Left offset must be greater than or equal to 0")
    BigDecimal leftOffsetMm;

    String maskImageUrl;
}
