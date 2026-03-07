package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.StickerCreateRequest;
import com.shirt.pod.model.dto.request.StickerUpdateRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.StickerDTO;
import com.shirt.pod.service.StickerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stickers")
@RequiredArgsConstructor
@Tag(name = "Sticker", description = "APIs for managing stickers")
public class StickerController {

    private final StickerService stickerService;

    @Operation(summary = "Get all stickers")
    @GetMapping
    public ApiResponse<List<StickerDTO>> getAll() {
        return ApiResponse.<List<StickerDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Stickers fetched successfully")
                .data(stickerService.getAll())
                .build();
    }

    @Operation(summary = "Get sticker by ID")
    @GetMapping("/{id}")
    public ApiResponse<StickerDTO> getById(@PathVariable Long id) {
        return ApiResponse.<StickerDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Sticker fetched successfully")
                .data(stickerService.getById(id))
                .build();
    }

    @Operation(summary = "Upload sticker image")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<StickerDTO> upload(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<StickerDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Sticker uploaded successfully")
                .data(stickerService.upload(file))
                .build();
    }

    @Operation(summary = "Create new sticker (by link - deprecated, prefer upload)")
    @PostMapping
    public ApiResponse<StickerDTO> create(@Valid @RequestBody StickerCreateRequest request) {
        return ApiResponse.<StickerDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Sticker created successfully")
                .data(stickerService.create(request))
                .build();
    }

    @Operation(summary = "Update sticker")
    @PutMapping("/{id}")
    public ApiResponse<StickerDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody StickerUpdateRequest request) {
        return ApiResponse.<StickerDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Sticker updated successfully")
                .data(stickerService.update(id, request))
                .build();
    }

    @Operation(summary = "Delete sticker")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        stickerService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Sticker deleted successfully")
                .build();
    }
}
