package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.DesignProductCreateRequest;
import com.shirt.pod.model.dto.request.DesignProductUpdateRequest;
import com.shirt.pod.model.dto.response.DesignProductDTO;

import java.util.List;

public interface DesignProductService {

    List<DesignProductDTO> getPublicDesigns();

    List<DesignProductDTO> getMyDesigns(Long userId);

    DesignProductDTO getById(Long id);

    DesignProductDTO create(DesignProductCreateRequest request, Long userId);

    DesignProductDTO update(Long id, DesignProductUpdateRequest request, Long userId);

    DesignProductDTO setPublic(Long id, boolean isPublic, Long userId);

    void delete(Long id, Long userId);
}
