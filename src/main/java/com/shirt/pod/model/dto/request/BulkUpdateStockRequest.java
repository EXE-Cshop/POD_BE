package com.shirt.pod.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BulkUpdateStockRequest {

    @NotEmpty(message = "Updates list cannot be empty")
    @Valid
    List<BulkUpdateStockItem> updates;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    public static class BulkUpdateStockItem {
        @jakarta.validation.constraints.NotNull(message = "Variant ID is required")
        Long variantId;

        @jakarta.validation.constraints.NotNull(message = "Quantity change is required")
        Integer quantityChange;

        String reason;
        String notes;
    }
}
