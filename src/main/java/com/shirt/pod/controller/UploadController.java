package com.shirt.pod.controller;

import com.shirt.pod.model.dto.response.UploadDTO;
import com.shirt.pod.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UploadController {
    
    private final UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<UploadDTO> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> uploadResult = uploadService.uploadImage(file);
        UploadDTO response = new UploadDTO(
                uploadResult.get("url"),
                uploadResult.get("publicId")
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/upload")
    public ResponseEntity<Map<String, Object>> deleteImage(@RequestParam("publicId") String publicId) {
        boolean deleted = uploadService.deleteImage(publicId);
        
        Map<String, Object> response = Map.of(
                "success", deleted,
                "message", deleted ? "Image deleted successfully" : "Failed to delete image",
                "publicId", publicId
        );
        
        return ResponseEntity.ok(response);
    }
}
