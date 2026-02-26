package com.shirt.pod.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UploadService {
    Map<String, String> uploadImage(MultipartFile file);

    /**
     * Upload image từ byte[] (file được generate trong backend).
     *
     * @param bytes       nội dung file
     * @param filename    tên file (không bắt buộc, dùng để đặt public_id dễ debug)
     * @param contentType content type, ví dụ "image/png"
     * @return map chứa ít nhất "url" và "publicId"
     */
    Map<String, String> uploadImageBytes(byte[] bytes, String filename, String contentType);

    boolean deleteImage(String publicId);
}
