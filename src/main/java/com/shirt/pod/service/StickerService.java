package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.StickerCreateRequest;
import com.shirt.pod.model.dto.request.StickerUpdateRequest;
import com.shirt.pod.model.dto.response.StickerDTO;

import java.util.List;

public interface StickerService {

    List<StickerDTO> getAll();

    StickerDTO getById(Long id);

    StickerDTO create(StickerCreateRequest request);

    StickerDTO update(Long id, StickerUpdateRequest request);

    void delete(Long id);
}
