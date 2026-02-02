package com.shirt.pod.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shirt.pod.dto.UploadResponse;
import com.shirt.pod.service.S3StorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UploadController {
    
    private final S3StorageService s3StorageService;
    
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = s3StorageService.uploadFile(file);
        UploadResponse response = new UploadResponse(fileUrl);
        return ResponseEntity.ok(response);
    }
}
