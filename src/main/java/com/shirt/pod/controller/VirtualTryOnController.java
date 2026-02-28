package com.shirt.pod.controller;

import com.shirt.pod.service.VirtualTryOnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST Controller for Virtual Try-On feature.
 * Accepts person image + garment image, returns AI-generated try-on result.
 */
@RestController
@RequestMapping("/api/v1/virtual-tryon")
@RequiredArgsConstructor
@Slf4j
public class VirtualTryOnController {

    private final VirtualTryOnService virtualTryOnService;

    /**
     * Process virtual try-on.
     *
     * @param personImage  The person/model photo (full body or upper body)
     * @param garmentImage The garment/clothing image to try on
     * @return The result image with the garment fitted onto the person
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> tryOn(
            @RequestParam("personImage") MultipartFile personImage,
            @RequestParam("garmentImage") MultipartFile garmentImage) {
        log.info("Virtual Try-On request received. Person: {}, Garment: {}",
                personImage.getOriginalFilename(), garmentImage.getOriginalFilename());

        // Validate inputs
        if (personImage.isEmpty() || garmentImage.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Both personImage and garmentImage are required"));
        }

        // Validate file types
        if (!isImageFile(personImage) || !isImageFile(garmentImage)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Only image files (PNG, JPG, JPEG, WEBP) are allowed"));
        }

        try {
            byte[] resultImage = virtualTryOnService.processVirtualTryOn(personImage, garmentImage);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header("Content-Disposition", "inline; filename=\"virtual-tryon-result.png\"")
                    .body(resultImage);

        } catch (Exception e) {
            log.error("Virtual Try-On failed", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

            // Detect specific error types
            if (errorMsg.contains("blocked") || errorMsg.contains("SAFETY")) {
                status = HttpStatus.UNPROCESSABLE_ENTITY;
            } else if (errorMsg.contains("rate") || errorMsg.contains("quota")) {
                status = HttpStatus.TOO_MANY_REQUESTS;
            }

            return ResponseEntity.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "error", "Virtual Try-On processing failed",
                            "message", errorMsg,
                            "status", status.value()));
        }
    }

    /**
     * Health check for the Virtual Try-On service.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
                "status", "available",
                "provider", "Kolors Virtual Try-On (HuggingFace)",
                "note", "Free tier — processing may take 30-120 seconds"));
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null)
            return false;
        return contentType.startsWith("image/");
    }
}
