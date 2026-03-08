package com.shirt.pod.service.impl;

import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.model.dto.request.StickerCreateRequest;
import com.shirt.pod.model.dto.request.StickerUpdateRequest;
import com.shirt.pod.model.dto.response.StickerDTO;
import com.shirt.pod.model.entity.Sticker;
import com.shirt.pod.repository.StickerRepository;
import com.shirt.pod.service.StickerService;
import com.shirt.pod.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StickerServiceImpl implements StickerService {

    private final StickerRepository stickerRepository;
    private final UploadService uploadService;

    @Override
    @Transactional(readOnly = true)
    public List<StickerDTO> getAll() {
        log.info("Fetching all stickers");
        return stickerRepository.findAllByOrderByIdAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StickerDTO upload(MultipartFile file) {
        log.info("Uploading sticker image");
        var result = uploadService.uploadSticker(file);
        String url = result.get("url");
        if (url == null || url.isBlank()) {
            throw new ResourceNotFoundException("Upload returned no URL");
        }
        String label = file.getOriginalFilename();
        if (label != null && label.contains(".")) {
            label = label.substring(0, label.lastIndexOf('.'));
        }
        if (label != null && label.length() > 255) {
            label = label.substring(0, 255);
        }
        Sticker sticker = Sticker.builder()
                .link(url)
                .label(label)
                .build();
        Sticker saved = stickerRepository.save(sticker);
        log.info("Created sticker from upload, id={}", saved.getId());
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StickerDTO getById(Long id) {
        log.info("Fetching sticker with id: {}", id);
        Sticker sticker = stickerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sticker not found with id: " + id));
        return toDTO(sticker);
    }

    @Override
    @Transactional
    public StickerDTO create(StickerCreateRequest request) {
        log.info("Creating new sticker with link: {}", request.getLink());
        Sticker sticker = Sticker.builder()
                .link(request.getLink())
                .build();
        Sticker saved = stickerRepository.save(sticker);
        log.info("Created sticker with id: {}", saved.getId());
        return toDTO(saved);
    }

    @Override
    @Transactional
    public StickerDTO update(Long id, StickerUpdateRequest request) {
        log.info("Updating sticker with id: {}", id);
        Sticker sticker = stickerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sticker not found with id: " + id));
        if (request.getLink() != null) {
            sticker.setLink(request.getLink());
        }
        if (request.getLabel() != null) {
            sticker.setLabel(request.getLabel().trim().isEmpty() ? null : request.getLabel().trim());
        }
        Sticker updated = stickerRepository.save(sticker);
        log.info("Updated sticker with id: {}", id);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting sticker with id: {}", id);
        Sticker sticker = stickerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sticker not found with id: " + id));
        String link = sticker.getLink();
        if (link != null && link.contains("cloudinary.com")) {
            try {
                boolean deleted = uploadService.deleteImageByUrl(link);
                log.info("Sticker id={}: removed from Cloudinary: {}", id, deleted);
            } catch (Exception e) {
                log.warn("Could not delete sticker image from Cloudinary: {}", e.getMessage());
            }
        }
        stickerRepository.delete(sticker);
        log.info("Deleted sticker with id: {}", id);
    }

    private StickerDTO toDTO(Sticker sticker) {
        return StickerDTO.builder()
                .id(sticker.getId())
                .link(sticker.getLink())
                .label(sticker.getLabel())
                .createdDate(sticker.getCreatedDate())
                .build();
    }
}
