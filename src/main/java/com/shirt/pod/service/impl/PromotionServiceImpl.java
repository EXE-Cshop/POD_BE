package com.shirt.pod.service.impl;

import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.exception.ValidationException;
import com.shirt.pod.model.dto.request.CreatePromotionRequest;
import com.shirt.pod.model.dto.request.UpdatePromotionRequest;
import com.shirt.pod.model.dto.response.PromotionDTO;
import com.shirt.pod.model.entity.Promotion;
import com.shirt.pod.model.entity.enums.DiscountType;
import com.shirt.pod.repository.PromotionRepository;
import com.shirt.pod.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<PromotionDTO> getActivePromotions() {
        return promotionRepository.findByActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public PromotionDTO getById(Long id) {
        return toDTO(promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found: " + id)));
    }

    @Override
    @Transactional
    public PromotionDTO create(CreatePromotionRequest request) {
        if (promotionRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new ValidationException("Promotion code already exists: " + request.getCode());
        }
        Promotion promotion = Promotion.builder()
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .discountType(DiscountType.valueOf(request.getDiscountType()))
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxUsageCount(request.getMaxUsageCount())
                .usedCount(0)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(true)
                .build();
        return toDTO(promotionRepository.save(promotion));
    }

    @Override
    @Transactional
    public PromotionDTO update(Long id, UpdatePromotionRequest request) {
        Promotion promo = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found: " + id));
        if (request.getCode() != null) promo.setCode(request.getCode().toUpperCase());
        if (request.getDescription() != null) promo.setDescription(request.getDescription());
        if (request.getDiscountType() != null) promo.setDiscountType(DiscountType.valueOf(request.getDiscountType()));
        if (request.getDiscountValue() != null) promo.setDiscountValue(request.getDiscountValue());
        if (request.getMinOrderAmount() != null) promo.setMinOrderAmount(request.getMinOrderAmount());
        if (request.getMaxUsageCount() != null) promo.setMaxUsageCount(request.getMaxUsageCount());
        if (request.getStartDate() != null) promo.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) promo.setEndDate(request.getEndDate());
        if (request.getActive() != null) promo.setActive(request.getActive());
        return toDTO(promotionRepository.save(promo));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion not found: " + id);
        }
        promotionRepository.deleteById(id);
    }

    @Override
    public PromotionDTO validateCode(String code) {
        Promotion promo = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid promotion code: " + code));
        assertPromotionUsable(promo);
        return toDTO(promo);
    }

    @Override
    public BigDecimal calculateDiscount(String code, BigDecimal orderTotal) {
        Promotion promo = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid promotion code: " + code));
        assertPromotionUsable(promo);
        if (promo.getMinOrderAmount() != null && orderTotal.compareTo(promo.getMinOrderAmount()) < 0) {
            return BigDecimal.ZERO;
        }
        if (promo.getDiscountType() == DiscountType.PERCENTAGE) {
            return orderTotal.multiply(promo.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        } else {
            return promo.getDiscountValue().min(orderTotal);
        }
    }

    @Override
    @Transactional
    public void markPromotionUsed(String code) {
        if (code == null || code.isBlank()) {
            return;
        }
        Promotion promo = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid promotion code: " + code));
        assertPromotionUsable(promo);
        promo.setUsedCount((promo.getUsedCount() == null ? 0 : promo.getUsedCount()) + 1);
        promotionRepository.save(promo);
    }

    private void assertPromotionUsable(Promotion promo) {
        if (!Boolean.TRUE.equals(promo.getActive())) {
            throw new ValidationException("Promotion is inactive");
        }
        Instant now = Instant.now();
        if (promo.getStartDate() != null && now.isBefore(promo.getStartDate())) {
            throw new ValidationException("Promotion has not started yet");
        }
        if (promo.getEndDate() != null && now.isAfter(promo.getEndDate())) {
            throw new ValidationException("Promotion has expired");
        }
        if (promo.getMaxUsageCount() != null
                && (promo.getUsedCount() == null ? 0 : promo.getUsedCount()) >= promo.getMaxUsageCount()) {
            throw new ValidationException("Promotion usage limit reached");
        }
    }

    private PromotionDTO toDTO(Promotion p) {
        return PromotionDTO.builder()
                .id(p.getId())
                .code(p.getCode())
                .description(p.getDescription())
                .discountType(p.getDiscountType().name())
                .discountValue(p.getDiscountValue())
                .minOrderAmount(p.getMinOrderAmount())
                .maxUsageCount(p.getMaxUsageCount())
                .usedCount(p.getUsedCount())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .active(p.getActive())
                .build();
    }
}
