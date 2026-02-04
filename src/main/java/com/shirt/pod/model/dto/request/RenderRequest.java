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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenderRequest {

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
    private List<DesignLayerRequest> layers;

    @JsonProperty("background_image_url")
    private String backgroundImageUrl;

    @Builder.Default
    private Integer dpi = 300;

    @JsonProperty("output_format")
    @Builder.Default
    private String outputFormat = "PNG";
}
