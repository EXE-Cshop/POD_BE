package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.ProductVariantCreateRequest;
import com.shirt.pod.model.dto.request.ProductVariantFilterRequest;
import com.shirt.pod.model.dto.request.ProductVariantUpdateRequest;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import org.springframework.data.domain.Page;

public interface ProductVariantService {

    Page<ProductVariantDTO> getAll(ProductVariantFilterRequest filterRequest);

    ProductVariantDTO getById(Long id);

    ProductVariantDTO create(ProductVariantCreateRequest request);

    ProductVariantDTO update(Long id, ProductVariantUpdateRequest request);

    void delete(Long id);
}
