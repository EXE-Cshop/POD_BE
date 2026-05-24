package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.CreatePromotionRequest;
import com.shirt.pod.model.dto.request.UpdatePromotionRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.PromotionDTO;
import com.shirt.pod.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotion", description = "Promotion and Coupon management APIs")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    @Operation(summary = "Get all promotions (Admin)")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROMOTION_VIEW')")
    public ApiResponse<List<PromotionDTO>> getAllPromotions() {
        List<PromotionDTO> promotions = promotionService.getAllPromotions();
        return ApiResponse.<List<PromotionDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("All promotions retrieved successfully")
                .data(promotions)
                .build();
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active promotions")
    public ApiResponse<List<PromotionDTO>> getActivePromotions() {
        List<PromotionDTO> promotions = promotionService.getActivePromotions();
        return ApiResponse.<List<PromotionDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Active promotions retrieved successfully")
                .data(promotions)
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get promotion by ID (Admin)")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROMOTION_VIEW')")
    public ApiResponse<PromotionDTO> getPromotionById(@PathVariable Long id) {
        PromotionDTO promotion = promotionService.getById(id);
        return ApiResponse.<PromotionDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Promotion retrieved successfully")
                .data(promotion)
                .build();
    }

    @PostMapping
    @Operation(summary = "Create a new promotion (Admin)")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROMOTION_CREATE')")
    public ApiResponse<PromotionDTO> createPromotion(@Valid @RequestBody CreatePromotionRequest request) {
        PromotionDTO promotion = promotionService.create(request);
        return ApiResponse.<PromotionDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Promotion created successfully")
                .data(promotion)
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing promotion (Admin)")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROMOTION_UPDATE')")
    public ApiResponse<PromotionDTO> updatePromotion(@PathVariable Long id, @Valid @RequestBody UpdatePromotionRequest request) {
        PromotionDTO promotion = promotionService.update(id, request);
        return ApiResponse.<PromotionDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Promotion updated successfully")
                .data(promotion)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a promotion (Admin)")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROMOTION_DELETE')")
    public ApiResponse<Void> deletePromotion(@PathVariable Long id) {
        promotionService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Promotion deleted successfully")
                .build();
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate a coupon code")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PromotionDTO> validateCoupon(@Valid @RequestBody ValidateCouponRequest request) {
        PromotionDTO promotion = promotionService.validateCode(request.getCode());
        return ApiResponse.<PromotionDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Coupon is valid")
                .data(promotion)
                .build();
    }

    @Data
    public static class ValidateCouponRequest {
        @NotBlank(message = "Coupon code is required")
        private String code;
    }
}
