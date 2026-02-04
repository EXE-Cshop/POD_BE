package com.shirt.pod.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenderResponse {

    @JsonProperty("file_url")
    private String fileUrl;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("width_px")
    private Integer widthPx;

    @JsonProperty("height_px")
    private Integer heightPx;

    private Integer dpi;

    @JsonProperty("render_time_ms")
    private Long renderTimeMs;

    @JsonProperty("rendered_at")
    private Instant renderedAt;

    private String status;

    @JsonProperty("error_message")
    private String errorMessage;
}