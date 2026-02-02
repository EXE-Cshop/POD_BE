package com.shirt.pod.model.dto.request;

import com.shirt.pod.model.entity.enums.PrintAreaName;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class PrintAreaCreateRequest implements Serializable {

    @NotNull(message = "Base product ID is required")
    Long baseProductId;

    @NotNull(message = "Print area name is required")
    PrintAreaName name;

    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.01", message = "Width must be greater than 0")
    BigDecimal widthMm;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.01", message = "Height must be greater than 0")
    BigDecimal heightMm;

    @Min(value = 0, message = "Top offset percent must be between 0 and 100")
    @Max(value = 100, message = "Top offset percent must be between 0 and 100")
    Double topOffsetPercent;

    @Min(value = 0, message = "Left offset percent must be between 0 and 100")
    @Max(value = 100, message = "Left offset percent must be between 0 and 100")
    Double leftOffsetPercent;

    @Min(value = 0, message = "Width percent must be between 0 and 100")
    @Max(value = 100, message = "Width percent must be between 0 and 100")
    Double widthPercent;

    @Min(value = 0, message = "Height percent must be between 0 and 100")
    @Max(value = 100, message = "Height percent must be between 0 and 100")
    Double heightPercent;

    String maskImageUrl;
}
