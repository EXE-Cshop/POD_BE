package com.shirt.pod.service;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.model.dto.request.CreateGiftRequest;
import com.shirt.pod.model.entity.GiftMessage;
import com.shirt.pod.model.entity.Order;
import com.shirt.pod.repository.GiftMessageRepository;
import com.shirt.pod.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftMessageService {

    private final GiftMessageRepository giftMessageRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public GiftMessage createGift(CreateGiftRequest request, Long userId) {
        // Verify order ownership
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        if (order.getUserId() == null || !order.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.RESOURCE_FORBIDDEN);
        }

        // Check if gift already exists for this order (Upsert logic)
        return giftMessageRepository.findByOrderId(request.getOrderId())
                .map(existingGift -> {
                    log.info("Updating existing gift message uuid={} for orderId={}", existingGift.getUuid(), request.getOrderId());
                    existingGift.setMediaUrl(request.getMediaUrl());
                    existingGift.setMessageText(request.getMessageText());
                    return giftMessageRepository.save(existingGift);
                })
                .orElseGet(() -> {
                    GiftMessage gift = GiftMessage.builder()
                            .orderId(request.getOrderId())
                            .uuid(UUID.randomUUID().toString())
                            .mediaUrl(request.getMediaUrl())
                            .messageText(request.getMessageText())
                            .build();

                    GiftMessage saved = giftMessageRepository.save(gift);
                    log.info("Created new gift message uuid={} for orderId={} by userId={}", saved.getUuid(), saved.getOrderId(), userId);
                    return saved;
                });
    }

    @Transactional(readOnly = true)
    public List<GiftMessage> getMyGifts(Long userId) {
        if (userId == null) return Collections.emptyList();
        
        List<Order> userOrders = orderRepository.findByUserId(userId);
        if (userOrders.isEmpty()) return Collections.emptyList();
        
        List<Long> orderIds = userOrders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
                
        return giftMessageRepository.findByOrderIdIn(orderIds);
    }

    @Transactional(readOnly = true)
    public GiftMessage getGiftByUuid(String uuid) {
        return giftMessageRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("GiftMessage not found with uuid: " + uuid));
    }
}
