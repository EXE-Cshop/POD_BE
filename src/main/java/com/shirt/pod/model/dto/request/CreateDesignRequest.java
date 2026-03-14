package com.shirt.pod.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDesignRequest {

    @NotNull(message = "creatorId is required")
    private Long creatorId;

    private String canvasData;

    private String previewUrl;

    private Boolean isPublic;

    private Long parentDesignId;
}
