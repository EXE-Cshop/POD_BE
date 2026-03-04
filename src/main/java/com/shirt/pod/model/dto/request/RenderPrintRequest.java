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

    /**
     * Optional: Garment/shirt image URL. When set, renders full mockup (shirt + design) at 300 DPI.
     */
    @JsonProperty("garment_image_url")
    private String garmentImageUrl;

    /**
     * Print area position on garment, as ratio 0-1. Default: DesignerPage layout (200,100,400,600 on 800x800).
     */
    @JsonProperty("print_area_left_ratio")
    @Builder.Default
    private Double printAreaLeftRatio = 0.25;

    @JsonProperty("print_area_top_ratio")
    @Builder.Default
    private Double printAreaTopRatio = 0.125;

    @JsonProperty("print_area_width_ratio")
    @Builder.Default
    private Double printAreaWidthRatio = 0.5;

    @JsonProperty("print_area_height_ratio")
    @Builder.Default
    private Double printAreaHeightRatio = 0.75;
}

