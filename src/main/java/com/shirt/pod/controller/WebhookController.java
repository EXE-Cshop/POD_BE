package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.OrderCompleteRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.service.OrderWebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final OrderWebhookService orderWebhookService;

    @PostMapping("/order-complete")
    public ResponseEntity<ApiResponse<Void>> handleOrderComplete(
            @Valid @RequestBody OrderCompleteRequest request) {
        orderWebhookService.handleOrderComplete(request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .data(null)
                .code(HttpStatus.OK.value())
                .message("Order processed successfully")
                .timestamp(Instant.now().toEpochMilli())
                .build();
        return ResponseEntity.ok(response);
    }
}
