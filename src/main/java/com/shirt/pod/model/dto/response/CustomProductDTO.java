package com.shirt.pod.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomProductDTO {
    private Long id;
    private Long baseProductId;
    private String baseProductName;
    private Long productVariantId;
    private String colorName;
    private String size;
    private String name;
    private String previewImageUrl;
    private String frontPrintUrl;
    private String backPrintUrl;
    private Instant createdDate;
}
