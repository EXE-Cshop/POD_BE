package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.CreateGiftRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.entity.GiftMessage;
import com.shirt.pod.security.CustomUserDetails;
import com.shirt.pod.service.GiftMessageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/gifts")
@RequiredArgsConstructor
public class GiftController {

    private final GiftMessageService giftMessageService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GiftMessage>> createGift(
            @Valid @RequestBody CreateGiftRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        GiftMessage gift = giftMessageService.createGift(request, userId);
        ApiResponse<GiftMessage> response = ApiResponse.<GiftMessage>builder()
                .data(gift)
                .code(HttpStatus.CREATED.value())
                .message("Create grift successfully")
                .timestamp(Instant.now().toEpochMilli())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<GiftMessage>>> getMyGifts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        List<GiftMessage> gifts = giftMessageService.getMyGifts(userId);
        ApiResponse<List<GiftMessage>> response = ApiResponse.<List<GiftMessage>>builder()
                .data(gifts)
                .code(HttpStatus.OK.value())
                .message("Get my grifts successfully")
                .timestamp(Instant.now().toEpochMilli())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<GiftMessage>> getGiftByUuid(@PathVariable String uuid) {
        GiftMessage gift = giftMessageService.getGiftByUuid(uuid);
        ApiResponse<GiftMessage> response = ApiResponse.<GiftMessage>builder()
                .data(gift)
                .code(HttpStatus.OK.value())
                .message("Get grift by uuid successfully")
                .timestamp(Instant.now().toEpochMilli())
                .build();
        return ResponseEntity.ok(response);
    }
}
