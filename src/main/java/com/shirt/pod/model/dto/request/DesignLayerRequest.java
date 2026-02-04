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


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignLayerRequest {

    @NotBlank(message = "Layer type is required")
    private String type;

    @NotBlank(message = "Layer URL is required")
    private String url;

    @NotNull(message = "X position is required")
    @PositiveOrZero(message = "X position must be >= 0")
    private BigDecimal x;

    @NotNull(message = "Y position is required")
    @PositiveOrZero(message = "Y position must be >= 0")
    private BigDecimal y;

    @NotNull(message = "Width is required")
    @PositiveOrZero(message = "Width must be > 0")
    private BigDecimal width;

    @NotNull(message = "Height is required")
    @PositiveOrZero(message = "Height must be > 0")
    private BigDecimal height;

    @NotNull(message = "Rotation is required")
    private BigDecimal rotation;

    @JsonProperty("z_index")
    private Integer zIndex;

    private BigDecimal opacity;

    private String text;
    private String fontFamily;
    private Integer fontSize;
    private String fontColor;

    private String shapeType;
    private String fillColor;
    private String strokeColor;
    private BigDecimal strokeWidth;
}
