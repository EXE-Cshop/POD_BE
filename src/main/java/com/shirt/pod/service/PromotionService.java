package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.CreatePromotionRequest;
import com.shirt.pod.model.dto.request.UpdatePromotionRequest;
import com.shirt.pod.model.dto.response.PromotionDTO;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionService {
    List<PromotionDTO> getAllPromotions();
    List<PromotionDTO> getActivePromotions();
    PromotionDTO getById(Long id);
    PromotionDTO create(CreatePromotionRequest request);
    PromotionDTO update(Long id, UpdatePromotionRequest request);
    void delete(Long id);
    PromotionDTO validateCode(String code);
    BigDecimal calculateDiscount(String code, BigDecimal orderTotal);
    void markPromotionUsed(String code);
}
