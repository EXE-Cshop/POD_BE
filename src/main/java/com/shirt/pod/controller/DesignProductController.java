package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.DesignProductCreateRequest;
import com.shirt.pod.model.dto.request.DesignProductUpdateRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.DesignProductDTO;
import com.shirt.pod.security.CustomUserDetails;
import com.shirt.pod.service.DesignProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/v1/design-products")
@RequiredArgsConstructor
@Tag(name = "Design Product", description = "APIs for saving and sharing custom designs")
@Slf4j
public class DesignProductController {

    private final DesignProductService designProductService;

    @Operation(summary = "Get all public designs (community gallery)")
    @GetMapping("/public")
    public ApiResponse<List<DesignProductDTO>> getPublicDesigns() {
        return ApiResponse.<List<DesignProductDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Public designs fetched successfully")
                .data(designProductService.getPublicDesigns())
                .build();
    }

    @Operation(summary = "Get my saved designs")
    @GetMapping("/my")
    public ApiResponse<List<DesignProductDTO>> getMyDesigns(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ApiResponse.<List<DesignProductDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("My designs fetched successfully")
                .data(designProductService.getMyDesigns(userId))
                .build();
    }

    @Operation(summary = "Get design by ID")
    @GetMapping("/{id}")
    public ApiResponse<DesignProductDTO> getById(@PathVariable Long id) {
        return ApiResponse.<DesignProductDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Design fetched successfully")
                .data(designProductService.getById(id))
                .build();
    }

    @Operation(summary = "Save new design")
    @PostMapping
    public ResponseEntity<ApiResponse<DesignProductDTO>> create(
            @Valid @RequestBody DesignProductCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("DesignProduct create request: name={}, baseProductId={}, isPublic={}", request.getName(), request.getBaseProductId(), request.getIsPublic());
        Long userId = userDetails != null ? userDetails.getId() : null;
        DesignProductDTO dto = designProductService.create(request, userId);
        log.info("DesignProduct created: id={}", dto.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<DesignProductDTO>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Design saved successfully")
                        .data(dto)
                        .build());
    }

    @Operation(summary = "Update design (name, isPublic, designJsonData)")
    @PutMapping("/{id}")
    public ApiResponse<DesignProductDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DesignProductUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        boolean hasDesignData = request.getDesignJsonData() != null && !request.getDesignJsonData().isEmpty();
        log.info("DesignProduct update id={} name={} hasDesignJsonData={} (will trigger render)", id, request.getName(), hasDesignData);
        return ApiResponse.<DesignProductDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Design updated successfully")
                .data(designProductService.update(id, request, userId))
                .build();
    }

    @Operation(summary = "Toggle public/private")
    @PatchMapping("/{id}/public")
    public ApiResponse<DesignProductDTO> setPublic(
            @PathVariable Long id,
            @RequestParam boolean value,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ApiResponse.<DesignProductDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Design visibility updated")
                .data(designProductService.setPublic(id, value, userId))
                .build();
    }

    @Operation(summary = "Delete design")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        designProductService.delete(id, userId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Design deleted successfully")
                .build();
    }
}
