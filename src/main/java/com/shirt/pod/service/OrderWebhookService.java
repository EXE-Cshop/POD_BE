package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.OrderCompleteRequest;
import com.shirt.pod.model.entity.Design;
import com.shirt.pod.repository.DesignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderWebhookService {

    private final DesignRepository designRepository;
    private final WalletService walletService;

    @Transactional
    public void handleOrderComplete(OrderCompleteRequest request) {
        log.info("Processing order-complete webhook for orderId={}", request.getOrderId());

        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.info("Order {} has no items to process", request.getOrderId());
            return;
        }

        BigDecimal pointsPerSale = walletService.getPointsPerRemixSale();

        for (OrderCompleteRequest.OrderItem item : request.getItems()) {
            if (item.getDesignId() == null) {
                continue;
            }

            Optional<Design> designOpt = designRepository.findById(item.getDesignId());
            if (designOpt.isEmpty()) {
                log.warn("Design id={} not found, skipping", item.getDesignId());
                continue;
            }

            Design design = designOpt.get();
            if (design.getParentDesignId() == null) {
                log.debug("Design id={} has no parentDesignId, skipping royalty", design.getId());
                continue;
            }

            Optional<Design> parentOpt = designRepository.findById(design.getParentDesignId());
            if (parentOpt.isEmpty()) {
                log.warn("Parent design id={} not found, skipping royalty", design.getParentDesignId());
                continue;
            }

            Design parentDesign = parentOpt.get();
            Long originalCreatorId = parentDesign.getCreatorId();
            int quantity = (item.getQuantity() != null && item.getQuantity() > 0) ? item.getQuantity() : 1;
            BigDecimal totalPoints = pointsPerSale.multiply(BigDecimal.valueOf(quantity));

            walletService.addPoints(originalCreatorId, totalPoints);
            log.info("Credited {} points to original creator userId={} for remix design id={} (qty={})",
                    totalPoints, originalCreatorId, design.getId(), quantity);
        }
    }
}
