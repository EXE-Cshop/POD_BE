package com.shirt.pod.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGiftRequest {

    @NotNull(message = "orderId is required")
    private Long orderId;

    private String mediaUrl;

    private String messageText;
}
