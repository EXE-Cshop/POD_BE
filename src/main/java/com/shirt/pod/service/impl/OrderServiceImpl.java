package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.mapper.OrderItemMapper;
import com.shirt.pod.mapper.OrderMapper;
import com.shirt.pod.model.dto.request.RenderPrintRequest;
import com.shirt.pod.model.dto.response.DashboardStatsDTO;
import com.shirt.pod.model.dto.response.OrderDTO;
import com.shirt.pod.model.dto.response.OrderDetailDTO;
import com.shirt.pod.model.dto.response.OrderItemDTO;
import com.shirt.pod.model.dto.response.RenderResponse;
import com.shirt.pod.model.entity.Order;
import com.shirt.pod.model.entity.enums.OrderStatus;
import com.shirt.pod.repository.OrderRepository;
import com.shirt.pod.service.OrderService;
import com.shirt.pod.service.RenderEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final OrderMapper orderMapper;
        private final OrderItemMapper orderItemMapper;
        private final RenderEngineService renderEngineService;

        @Override
        public Page<OrderDTO> getOrders(OrderStatus status, int page, int size, String sortBy, String order) {
                Sort.Direction direction = Sort.Direction.fromString(order.toUpperCase());
                Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size,
                                Sort.by(direction, sortBy));
                log.info("Fetching orders with status: {}, pageable: {}", status, pageable);

                Page<Order> orderPage = (status != null)
                                ? orderRepository.findByStatus(status, pageable)
                                : orderRepository.findAll(pageable);

                log.info("Found {} orders (page {}/{}, size {})",
                                orderPage.getTotalElements(),
                                orderPage.getNumber(),
                                orderPage.getTotalPages(),
                                orderPage.getSize());

                return orderPage.map(orderMapper::toDTO);
        }

        @Override
        @Transactional(readOnly = true)
        public DashboardStatsDTO getDashboardStats() {
                log.info("Fetching dashboard statistics");

                Long totalOrders = orderRepository.count();
                Long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
                Long paidOrders = orderRepository.countByStatus(OrderStatus.PAID);
                Long processingOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
                Long shippedOrders = orderRepository.countByStatus(OrderStatus.SHIPPED);
                Long completedOrders = orderRepository.countByStatus(OrderStatus.COMPLETED);
                Long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

                BigDecimal totalRevenue = orderRepository.sumTotalRevenue();
                BigDecimal pendingRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.PENDING);
                BigDecimal paidRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.PAID);
                BigDecimal completedRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED);

                log.info("Dashboard stats - Total orders: {}, Total revenue: {}", totalOrders, totalRevenue);

                return DashboardStatsDTO.builder()
                                .totalOrders(totalOrders)
                                .pendingOrders(pendingOrders)
                                .paidOrders(paidOrders)
                                .processingOrders(processingOrders)
                                .shippedOrders(shippedOrders)
                                .completedOrders(completedOrders)
                                .cancelledOrders(cancelledOrders)
                                .totalRevenue(totalRevenue)
                                .pendingRevenue(pendingRevenue)
                                .paidRevenue(paidRevenue)
                                .completedRevenue(completedRevenue)
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public OrderDetailDTO getOrderDetail(Long orderId) {
                log.info("Fetching order detail for orderId: {}", orderId);

                Order order = orderRepository.findByIdWithItems(orderId)
                                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "id",
                                                orderId.toString()));

                List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                                .map(orderItemMapper::toDTO)
                                .collect(Collectors.toList());

                log.info("Found order {} with {} items", orderId, orderItemDTOs.size());

                return OrderDetailDTO.builder()
                                .id(order.getId())
                                .status(order.getStatus())
                                .totalAmount(order.getTotalAmount())
                                .shippingFee(order.getShippingFee())
                                .recipientName(order.getRecipientName())
                                .recipientPhone(order.getRecipientPhone())
                                .shippingAddress(order.getShippingAddress())
                                .paymentMethod(order.getPaymentMethod())
                                .paymentStatus(order.getPaymentStatus())
                                .note(order.getNote())
                                .userId(order.getUserId())
                                .createdDate(order.getCreatedDate())
                                .orderItems(orderItemDTOs)
                                .build();
        }

        @Override
        @Transactional
        public void processPendingOrders() {
                log.info("Starting scheduled task: Processing PENDING orders");

                List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);

                if (pendingOrders.isEmpty()) {
                        log.info("No PENDING orders found to process");
                        return;
                }

                log.info("Found {} PENDING orders to process", pendingOrders.size());

                int successCount = 0;
                int failureCount = 0;

                for (Order order : pendingOrders) {
                        try {
                                log.info("Processing order ID: {}", order.getId());

                                for (var orderItem : order.getOrderItems()) {
                                        if (orderItem.getPrintFileUrl() != null
                                                        && !orderItem.getPrintFileUrl().isBlank()) {
                                                log.info("Triggering render for OrderItem ID: {}", orderItem.getId());

                                                // TODO: Build RenderRequest từ OrderItem data
                                                // Tạm thời skip render nếu không có đủ data
                                                // RenderResponse response =
                                                // renderEngineService.renderDesign(renderRequest);
                                                // orderItem.setPrintFileUrl(response.getFileUrl());
                                        }
                                }

                                order.setStatus(OrderStatus.PAID);
                                orderRepository.save(order);

                                log.info("Successfully processed order ID: {} -> Status changed to PAID",
                                                order.getId());
                                successCount++;

                        } catch (Exception e) {
                                log.error("Failed to process order ID: {}", order.getId(), e);
                                failureCount++;
                        }
                }

                log.info("Scheduled task completed - Success: {}, Failed: {}", successCount, failureCount);
        }
}
