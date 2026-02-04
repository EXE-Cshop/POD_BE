package com.shirt.pod.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png"
    );
    private static final String UPLOAD_FOLDER = "tshirt-pod/uploads";
    
    private final Cloudinary cloudinary;
    
    @Override
    public Map<String, String> uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_CORRUPTED);
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            double maxSizeMB = MAX_FILE_SIZE / (1024.0 * 1024.0);
            double actualSizeMB = file.getSize() / (1024.0 * 1024.0);
            throw new AppException(ErrorCode.FILE_TOO_LARGE, maxSizeMB, actualSizeMB);
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new AppException(
                    ErrorCode.INVALID_FILE_FORMAT, 
                    "JPG/JPEG/PNG", 
                    contentType != null ? contentType : "unknown"
            );
        }
        
        try {
            String uniqueFilename = UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
            
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", UPLOAD_FOLDER,
                    "public_id", uniqueFilename,
                    "resource_type", "image",
                    "overwrite", false,
                    "use_filename", false
            );
            
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            
            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            
            log.info("File uploaded successfully to Cloudinary. PublicId: {}, URL: {}", publicId, url);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("publicId", publicId);
            
            return result;
            
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while uploading file to Cloudinary: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
        }
    }
    
    @Override
    public boolean deleteImage(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "publicId");
        }
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String resultStatus = (String) result.get("result");
            
            boolean isDeleted = "ok".equalsIgnoreCase(resultStatus);
            
            if (isDeleted) {
                log.info("Image deleted successfully from Cloudinary. PublicId: {}", publicId);
            } else {
                log.warn("Failed to delete image from Cloudinary. PublicId: {}, Status: {}", publicId, resultStatus);
            }
            
            return isDeleted;
            
        } catch (Exception e) {
            log.error("Error while deleting image from Cloudinary: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.STORAGE_SERVICE_ERROR, e.getMessage());
        }
    }
}
