package com.shirt.pod.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * A design layer expressed in millimeters (mm) within a print area.
 * This is intended for production/print files (POD), not for screen-pixel preview.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrintDesignLayerRequest {

    @NotBlank(message = "Layer type is required")
    private String type; // image | text

    private String url; // required for image

    @NotNull(message = "X (mm) is required")
    @JsonProperty("x_mm")
    private BigDecimal xMm;

    @NotNull(message = "Y (mm) is required")
    @JsonProperty("y_mm")
    private BigDecimal yMm;

    @NotNull(message = "Width (mm) is required")
    @JsonProperty("width_mm")
    @PositiveOrZero(message = "Width (mm) must be > 0")
    private BigDecimal widthMm;

    @NotNull(message = "Height (mm) is required")
    @JsonProperty("height_mm")
    @PositiveOrZero(message = "Height (mm) must be > 0")
    private BigDecimal heightMm;

    @NotNull(message = "Rotation is required")
    @JsonProperty("rotation_deg")
    private BigDecimal rotationDeg;

    @JsonProperty("z_index")
    private Integer zIndex;

    private BigDecimal opacity;

    // Text fields
    private String text;
    private String fontFamily;
    private Integer fontSize;
    private String fontColor;
}

