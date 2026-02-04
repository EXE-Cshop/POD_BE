package com.shirt.pod.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UploadService {
    Map<String, String> uploadImage(MultipartFile file);
    boolean deleteImage(String publicId);
}
