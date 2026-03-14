package com.shirt.pod.service;

import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.model.dto.request.CreateDesignRequest;
import com.shirt.pod.model.entity.Design;
import com.shirt.pod.repository.DesignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DesignService {

    private final DesignRepository designRepository;

    @Transactional(readOnly = true)
    public Page<Design> getPublicFeed(Pageable pageable) {
        return designRepository.findByIsPublicTrue(pageable);
    }

    @Transactional
    public Design createDesign(CreateDesignRequest request) {
        Design design = Design.builder()
                .creatorId(request.getCreatorId())
                .canvasData(request.getCanvasData())
                .previewUrl(request.getPreviewUrl())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .parentDesignId(request.getParentDesignId())
                .build();

        Design saved = designRepository.save(design);
        log.info("Created design id={} by creator={}", saved.getId(), saved.getCreatorId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Design getDesignById(Long id) {
        return designRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design not found with id: " + id));
    }
}
