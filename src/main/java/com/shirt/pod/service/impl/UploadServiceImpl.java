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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );
    private static final String UPLOAD_FOLDER = "tshirt-pod/uploads";
    private static final String STICKER_FOLDER = "tshirt-pod/stickers";
    
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
            return doUpload(file.getBytes(), file.getContentType(), UPLOAD_FOLDER);
            
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while uploading file to Cloudinary: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
        }
    }

    @Override
    public Map<String, String> uploadSticker(MultipartFile file) {
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
                    "JPG/JPEG/PNG/WebP",
                    contentType != null ? contentType : "unknown"
            );
        }
        try {
            return doUpload(file.getBytes(), file.getContentType(), STICKER_FOLDER);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error uploading sticker to Cloudinary: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
        }
    }

    @Override
    public Map<String, String> uploadImageBytes(byte[] bytes, String filename, String contentType) {
        if (bytes == null || bytes.length == 0) {
            throw new AppException(ErrorCode.FILE_CORRUPTED);
        }

        if (bytes.length > MAX_FILE_SIZE) {
            double maxSizeMB = MAX_FILE_SIZE / (1024.0 * 1024.0);
            double actualSizeMB = bytes.length / (1024.0 * 1024.0);
            throw new AppException(ErrorCode.FILE_TOO_LARGE, maxSizeMB, actualSizeMB);
        }

        String ct = contentType != null ? contentType.toLowerCase() : null;
        if (ct == null || !ALLOWED_CONTENT_TYPES.contains(ct)) {
            throw new AppException(
                    ErrorCode.INVALID_FILE_FORMAT,
                    "JPG/JPEG/PNG",
                    ct != null ? ct : "unknown"
            );
        }

        try {
            return doUpload(bytes, ct, UPLOAD_FOLDER);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while uploading raw bytes to Cloudinary: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
        }
    }

    /**
     * Logic upload chung lên Cloudinary cho cả MultipartFile và byte[].
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> doUpload(byte[] bytes, String contentType, String folder) throws Exception {
        String uniqueFilename = UUID.randomUUID().toString() + "_" + System.currentTimeMillis();

        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folder,
                "public_id", uniqueFilename,
                "resource_type", "image",
                "overwrite", false,
                "use_filename", false
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(bytes, uploadParams);

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        log.info("File uploaded successfully to Cloudinary. ContentType: {}, PublicId: {}, URL: {}", contentType, publicId, url);

        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("publicId", publicId);

        return result;
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

    @Override
    public boolean deleteImageByUrl(String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isBlank() || !cloudinaryUrl.contains("cloudinary.com")) {
            return false;
        }
        String publicId = extractPublicIdFromUrl(cloudinaryUrl);
        if (publicId == null || publicId.isBlank()) {
            log.warn("Could not extract publicId from Cloudinary URL: {}", cloudinaryUrl.length() > 100 ? cloudinaryUrl.substring(0, 100) + "..." : cloudinaryUrl);
            return false;
        }
        try {
            return deleteImage(publicId);
        } catch (Exception e) {
            log.warn("Failed to delete image by URL (publicId={}): {}", publicId, e.getMessage());
            return false;
        }
    }

    private static final Pattern PUBLIC_ID_PATTERN = Pattern.compile("/upload/(?:[^/]+/)*v\\d+/(.+?)(?:\\.[^.]+)?$");

    /**
     * Trích public_id từ Cloudinary URL.
     * Ví dụ: https://res.cloudinary.com/xxx/image/upload/v123/folder/file.png -> folder/file
     */
    private String extractPublicIdFromUrl(String url) {
        Matcher m = PUBLIC_ID_PATTERN.matcher(url);
        if (!m.find()) return null;
        String publicId = m.group(1);
        return publicId == null || publicId.isBlank() ? null : publicId;
    }
}
