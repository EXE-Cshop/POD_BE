package com.shirt.pod.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Render request for production print files (POD).
 * All layer coordinates/dimensions are expressed in millimeters (mm).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenderPrintRequest {

    @NotNull(message = "Width (mm) is required")
    @JsonProperty("width_mm")
    @DecimalMin(value = "0.1", message = "Width must be positive")
    private BigDecimal widthMm;

    @NotNull(message = "Height (mm) is required")
    @JsonProperty("height_mm")
    @DecimalMin(value = "0.1", message = "Height must be positive")
    private BigDecimal heightMm;

    @NotEmpty(message = "Design layers cannot be empty")
    @Valid
    private List<PrintDesignLayerRequest> layers;

    /**
     * Output DPI for rasterization. Typical: 300.
     */
    @Builder.Default
    private Integer dpi = 300;

    /**
     * Output image format. Currently only PNG is supported.
     */
    @JsonProperty("output_format")
    @Builder.Default
    private String outputFormat = "PNG";

    /**
     * For print files, background is usually transparent.
     */
    @JsonProperty("transparent_background")
    @Builder.Default
    private Boolean transparentBackground = true;
}

