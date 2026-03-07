package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.model.document.DesignJsonDocument;
import com.shirt.pod.model.dto.request.DesignProductCreateRequest;
import com.shirt.pod.model.dto.request.DesignProductUpdateRequest;
import com.shirt.pod.model.dto.request.PrintDesignLayerRequest;
import com.shirt.pod.model.dto.request.RenderPrintRequest;
import com.shirt.pod.model.dto.response.DesignProductDTO;
import com.shirt.pod.model.dto.response.RenderResponse;
import com.shirt.pod.model.entity.BaseProduct;
import com.shirt.pod.model.entity.DesignProduct;
import com.shirt.pod.model.entity.User;
import com.shirt.pod.repository.DesignJsonRepository;
import com.shirt.pod.repository.DesignProductRepository;
import com.shirt.pod.repository.UserRepository;
import com.shirt.pod.repository.BaseProductRepository;
import com.shirt.pod.service.DesignProductService;
import com.shirt.pod.service.RenderEngineService;
import com.shirt.pod.service.UploadService;
import com.shirt.pod.utils.DesignToRenderLayersConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DesignProductServiceImpl implements DesignProductService {

    private static final BigDecimal PRINT_WIDTH_MM = new BigDecimal("100");
    private static final BigDecimal PRINT_HEIGHT_MM = new BigDecimal("150");
    private static final String GARMENT_WHITE = "https://res.cloudinary.com/di5j3h6wi/image/upload/v1772618145/MauAoTrang2_hd2m4x.jpg";
    private static final String GARMENT_BLACK = "https://res.cloudinary.com/di5j3h6wi/image/upload/v1772617682/b2a8c03b0b0761bb2bf0e4e6e7d5774b_nrk6ub.webp";

    private final DesignProductRepository designProductRepository;
    private final DesignJsonRepository designJsonRepository;
    private final UserRepository userRepository;
    private final BaseProductRepository baseProductRepository;
    private final RenderEngineService renderEngineService;
    private final UploadService uploadService;

    @Override
    @Transactional(readOnly = true)
    public List<DesignProductDTO> getPublicDesigns() {
        return designProductRepository.findByIsPublicTrueOrderByCreatedDateDesc().stream()
                .map(this::toDTOWithDesign)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesignProductDTO> getMyDesigns(Long userId) {
        if (userId == null) {
            return designProductRepository.findByUserIdIsNullOrderByCreatedDateDesc().stream()
                    .map(this::toDTOWithDesign)
                    .toList();
        }
        return designProductRepository.findByUserIdOrderByCreatedDateDesc(userId).stream()
                .map(this::toDTOWithDesign)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DesignProductDTO getById(Long id) {
        DesignProduct dp = designProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design not found with id: " + id));
        return toDTOWithDesign(dp);
    }

    @Override
    @Transactional
    public DesignProductDTO create(DesignProductCreateRequest request, Long userId) {
        Map<String, Object> designData = request.getDesignJsonData();
        if (designData == null || designData.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "designJsonData");
        }

        // 1. Lưu design JSON vào MongoDB
        DesignJsonDocument doc = DesignJsonDocument.builder()
                .data(designData)
                .build();
        doc = designJsonRepository.save(doc);
        String designJsonRef = doc.getId();
        log.info("Saved design JSON to MongoDB: {}", designJsonRef);

        // 2. Gọi render để tạo ảnh preview
        String previewUrl = null;
        try {
            String designSide = String.valueOf(designData.getOrDefault("designSide", "front"));
            List<PrintDesignLayerRequest> layers = DesignToRenderLayersConverter.convert(designData, designSide);
            if (layers.isEmpty()) {
                designSide = "front".equals(designSide) ? "back" : "front";
                layers = DesignToRenderLayersConverter.convert(designData, designSide);
            }
            if (!layers.isEmpty()) {
                String garmentUrl = request.getGarmentImageUrl();
                if (garmentUrl == null || garmentUrl.isBlank()) {
                    String color = String.valueOf(designData.getOrDefault("garmentColor", "white"));
                    garmentUrl = (color != null && color.toLowerCase().contains("black")) ? GARMENT_BLACK : GARMENT_WHITE;
                }
                RenderPrintRequest renderReq = RenderPrintRequest.builder()
                        .widthMm(PRINT_WIDTH_MM)
                        .heightMm(PRINT_HEIGHT_MM)
                        .layers(layers)
                        .dpi(300)
                        .garmentImageUrl(garmentUrl)
                        .printAreaLeftRatio(0.309)
                        .printAreaTopRatio(0.256)
                        .printAreaWidthRatio(0.381)
                        .printAreaHeightRatio(0.625)
                        .build();
                RenderResponse renderRes = renderEngineService.renderPrintFile(renderReq);
                String rawUrl = renderRes != null ? renderRes.getFileUrl() : null;
                if (rawUrl != null && (rawUrl.startsWith("http://") || rawUrl.startsWith("https://"))) {
                    previewUrl = rawUrl;
                    log.info("Rendered preview uploaded to Cloudinary: {}", previewUrl);
                } else if (rawUrl != null) {
                    log.warn("Render saved locally (not Cloudinary), preview not usable for frontend");
                }
            }
        } catch (Exception e) {
            log.warn("Preview render failed, continuing without: {}", e.getMessage());
        }

        // 3. Lưu metadata vào PostgreSQL
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        BaseProduct baseProduct = request.getBaseProductId() != null
                ? baseProductRepository.findById(request.getBaseProductId()).orElse(null)
                : null;

        DesignProduct dp = DesignProduct.builder()
                .user(user)
                .baseProduct(baseProduct)
                .name(request.getName())
                .designJsonRef(designJsonRef)
                .previewImageUrl(previewUrl)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .build();
        DesignProduct saved = designProductRepository.save(dp);
        log.info("Created design product id={} name={} preview={}", saved.getId(), saved.getName(), previewUrl);
        return toDTO(saved, designData);
    }

    @Override
    @Transactional
    public DesignProductDTO update(Long id, DesignProductUpdateRequest request, Long userId) {
        DesignProduct dp = designProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design not found with id: " + id));
        ensureOwner(dp, userId);

        if (request.getName() != null) dp.setName(request.getName());
        if (request.getIsPublic() != null) dp.setIsPublic(request.getIsPublic());

        Map<String, Object> designData = null;
        if (request.getDesignJsonData() != null && !request.getDesignJsonData().isEmpty()) {
            log.info("Update design id={}: designJsonData provided, will save to MongoDB and run render for preview", id);
            DesignJsonDocument doc = DesignJsonDocument.builder()
                    .data(request.getDesignJsonData())
                    .build();
            doc = designJsonRepository.save(doc);
            String oldRef = dp.getDesignJsonRef();
            dp.setDesignJsonRef(doc.getId());
            designData = request.getDesignJsonData();
            try {
                if (oldRef != null && !oldRef.equals(doc.getId())) designJsonRepository.deleteById(oldRef);
            } catch (Exception e) {
                log.warn("Could not delete old design doc {}: {}", oldRef, e.getMessage());
            }
            String previewUrl = null;
            try {
                String designSide = String.valueOf(designData.getOrDefault("designSide", "front"));
                List<PrintDesignLayerRequest> layers = DesignToRenderLayersConverter.convert(designData, designSide);
                if (layers.isEmpty()) {
                    designSide = "front".equals(designSide) ? "back" : "front";
                    layers = DesignToRenderLayersConverter.convert(designData, designSide);
                }
                log.info("Update design id={}: converted to {} layers for side={}", id, layers.size(), designSide);
                if (!layers.isEmpty()) {
                    String garmentUrl = request.getGarmentImageUrl();
                    if (garmentUrl == null || garmentUrl.isBlank()) {
                        String color = String.valueOf(designData.getOrDefault("garmentColor", "white"));
                        garmentUrl = (color != null && color.toLowerCase().contains("black")) ? GARMENT_BLACK : GARMENT_WHITE;
                    }
                    RenderPrintRequest renderReq = RenderPrintRequest.builder()
                            .widthMm(PRINT_WIDTH_MM)
                            .heightMm(PRINT_HEIGHT_MM)
                            .layers(layers)
                            .dpi(300)
                            .garmentImageUrl(garmentUrl)
                            .printAreaLeftRatio(0.309)
                            .printAreaTopRatio(0.256)
                            .printAreaWidthRatio(0.381)
                            .printAreaHeightRatio(0.625)
                            .build();
                    log.info("Update design id={}: calling renderEngineService.renderPrintFile", id);
                    RenderResponse renderRes = renderEngineService.renderPrintFile(renderReq);
                    previewUrl = renderRes != null ? renderRes.getFileUrl() : null;
                    log.info("Update design id={}: render completed, previewUrl={}", id, previewUrl);
                } else {
                    log.warn("Update design id={}: no layers to render, skipping preview generation", id);
                }
            } catch (Exception e) {
                log.warn("Preview render failed on update: {}", e.getMessage(), e);
            }
            if (previewUrl != null && (previewUrl.startsWith("http://") || previewUrl.startsWith("https://"))) {
                String oldPreview = dp.getPreviewImageUrl();
                if (oldPreview != null && oldPreview.contains("cloudinary.com") && !oldPreview.equals(previewUrl)) {
                    boolean deleted = uploadService.deleteImageByUrl(oldPreview);
                    log.info("Update design id={}: old preview deleted from Cloudinary: {}", id, deleted);
                }
                dp.setPreviewImageUrl(previewUrl);
                log.info("Update design id={}: preview URL updated (Cloudinary)", id);
            } else if (previewUrl != null) {
                log.warn("Update design id={}: previewUrl is local path, not usable for frontend - keeping old preview", id);
            } else {
                log.warn("Update design id={}: previewUrl is null, keeping old preview (if any)", id);
            }
        }

        DesignProduct updated = designProductRepository.save(dp);
        log.info("Updated design product id={}", id);
        return toDTO(updated, designData != null ? designData : fetchDesignFromMongo(updated.getDesignJsonRef()));
    }

    @Override
    @Transactional
    public DesignProductDTO setPublic(Long id, boolean isPublic, Long userId) {
        DesignProduct dp = designProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design not found with id: " + id));
        ensureOwner(dp, userId);
        dp.setIsPublic(isPublic);
        DesignProduct updated = designProductRepository.save(dp);
        log.info("Set design product id={} isPublic={}", id, isPublic);
        return toDTOWithDesign(updated);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        DesignProduct dp = designProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design not found with id: " + id));
        ensureOwner(dp, userId);
        String previewUrl = dp.getPreviewImageUrl();
        if (previewUrl != null && previewUrl.contains("cloudinary.com")) {
            try {
                boolean deleted = uploadService.deleteImageByUrl(previewUrl);
                log.info("Deleted design id={}: preview image removed from Cloudinary: {}", id, deleted);
            } catch (Exception e) {
                log.warn("Could not delete preview from Cloudinary: {}", e.getMessage());
            }
        }
        try {
            designJsonRepository.deleteById(dp.getDesignJsonRef());
        } catch (Exception e) {
            log.warn("Could not delete MongoDB design doc {}: {}", dp.getDesignJsonRef(), e.getMessage());
        }
        designProductRepository.delete(dp);
        log.info("Deleted design product id={}", id);
    }

    private void ensureOwner(DesignProduct dp, Long userId) {
        if (dp.getUser() == null) {
            if (userId != null) throw new AppException(ErrorCode.RESOURCE_FORBIDDEN);
            return;
        }
        if (userId == null || !dp.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.RESOURCE_FORBIDDEN);
        }
    }

    private DesignProductDTO toDTOWithDesign(DesignProduct dp) {
        Map<String, Object> designData = fetchDesignFromMongo(dp.getDesignJsonRef());
        return toDTO(dp, designData);
    }

    private Map<String, Object> fetchDesignFromMongo(String ref) {
        if (ref == null || ref.isBlank()) return null;
        return designJsonRepository.findById(ref)
                .map(DesignJsonDocument::getData)
                .orElse(null);
    }

    private DesignProductDTO toDTO(DesignProduct dp, Map<String, Object> designJsonData) {
        return DesignProductDTO.builder()
                .id(dp.getId())
                .userId(dp.getUser() != null ? dp.getUser().getId() : null)
                .creatorName(dp.getUser() != null ? dp.getUser().getFullName() : null)
                .baseProductId(dp.getBaseProduct() != null ? dp.getBaseProduct().getId() : null)
                .baseProductName(dp.getBaseProduct() != null ? dp.getBaseProduct().getName() : null)
                .name(dp.getName())
                .designJsonData(designJsonData)
                .previewImageUrl(dp.getPreviewImageUrl())
                .isPublic(dp.getIsPublic())
                .createdDate(dp.getCreatedDate())
                .build();
    }
}
