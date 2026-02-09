package com.shirt.pod.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shirt.pod.config.S3Config;
import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png"
    );
    
    private final S3Client s3Client;
    private final S3Config s3Config;
    
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_CORRUPTED);
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            double maxSizeMB = MAX_FILE_SIZE / (1024.0 * 1024.0);
            double actualSizeMB = file.getSize() / (1024.0 * 1024.0);
            throw new AppException(ErrorCode.FILE_TOO_LARGE, maxSizeMB, actualSizeMB);
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new AppException(ErrorCode.INVALID_FILE_FORMAT, "JPG/PNG", contentType != null ? contentType : "unknown");
        }
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(uniqueFilename)
                    .contentType(contentType)
                    .build();
            
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            String fileUrl = String.format("%s/%s/%s",
                    s3Config.getEndpoint(), 
                    s3Config.getBucketName(), 
                    uniqueFilename);
            
            log.info("File uploaded successfully: {}", fileUrl);
            return fileUrl;
            
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while uploading file: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex);
    }

    /**
     * Upload arbitrary byte[] as an object to S3 and return its URL.
     * Dùng cho các file được generate trong backend (ví dụ ảnh render).
     */
    public String uploadBytes(byte[] data, String contentType, String fileExtension) {
        if (data == null || data.length == 0) {
            throw new AppException(ErrorCode.FILE_CORRUPTED);
        }

        try {
            String safeExtension = (fileExtension == null || fileExtension.isBlank())
                    ? ""
                    : (fileExtension.startsWith(".") ? fileExtension : "." + fileExtension);

            String key = UUID.randomUUID() + safeExtension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(key)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));

            String url = String.format("%s/%s/%s",
                    s3Config.getEndpoint(),
                    s3Config.getBucketName(),
                    key);
            log.info("Bytes uploaded successfully to S3: key={}, size={}, contentType={}",
                    key, data.length, contentType);
            return url;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while uploading bytes to S3: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
        }
    }
}
