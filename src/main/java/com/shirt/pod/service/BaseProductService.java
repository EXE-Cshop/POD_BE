package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.BaseProductCreateRequest;
import com.shirt.pod.model.dto.request.BaseProductFilterRequest;
import com.shirt.pod.model.dto.request.BaseProductUpdateRequest;
import com.shirt.pod.model.dto.response.BaseProductDTO;
import org.springframework.data.domain.Page;

public interface BaseProductService {

    Page<BaseProductDTO> getAll(BaseProductFilterRequest filterRequest);

    BaseProductDTO getById(Long id);

    BaseProductDTO create(BaseProductCreateRequest request);

    BaseProductDTO update(Long id, BaseProductUpdateRequest request);

    void delete(Long id);
}
