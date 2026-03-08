package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.StickerCreateRequest;
import com.shirt.pod.model.dto.request.StickerUpdateRequest;
import com.shirt.pod.model.dto.response.StickerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StickerService {

    List<StickerDTO> getAll();

    /** Upload sticker ảnh lên Cloudinary và lưu vào kho */
    StickerDTO upload(MultipartFile file);

    StickerDTO getById(Long id);

    StickerDTO create(StickerCreateRequest request);

    StickerDTO update(Long id, StickerUpdateRequest request);

    void delete(Long id);
}
