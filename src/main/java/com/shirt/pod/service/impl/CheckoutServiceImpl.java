package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.model.dto.request.CheckoutRequest;
import com.shirt.pod.model.dto.response.OrderDTO;
import com.shirt.pod.model.entity.Cart;
import com.shirt.pod.model.entity.CartItem;
import com.shirt.pod.model.entity.Order;
import com.shirt.pod.model.entity.OrderItem;
import com.shirt.pod.model.entity.ProductVariant;
import com.shirt.pod.model.entity.User;
import com.shirt.pod.model.entity.enums.OrderStatus;
import com.shirt.pod.repository.CartRepository;
import com.shirt.pod.repository.OrderRepository;
import com.shirt.pod.repository.OrderItemRepository;
import com.shirt.pod.repository.UserRepository;
import com.shirt.pod.service.CartService;
import com.shirt.pod.service.CheckoutService;
import com.shirt.pod.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final PromotionService promotionService;

    @Override
    @Transactional
    public OrderDTO checkout(Long userId, CheckoutRequest request) {
        // Get user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_CART);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        for (CartItem cartItem : cart.getItems()) {
            ensurePurchasable(cartItem.getProductVariant(), cartItem.getQuantity());
        }

        BigDecimal totalAmount = cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply Promotion if present
        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedPromotionCode = null;
        if (request.getPromotionCode() != null && !request.getPromotionCode().trim().isEmpty()) {
            discountAmount = promotionService.calculateDiscount(request.getPromotionCode().trim(), totalAmount);
            appliedPromotionCode = request.getPromotionCode().trim();
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        // Create order
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalAmount(finalAmount)
                .shippingFee(BigDecimal.ZERO)
                .recipientName(user.getFullName())
                .recipientPhone(user.getPhoneNumber())
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod().name())
                .paymentStatus("UNPAID")
                .note(request.getNote())
                .promotionCode(appliedPromotionCode)
                .discountAmount(discountAmount)
                .build();

        Order savedOrder = orderRepository.save(order);

        // Create order items from cart items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getProductVariant();
            int available = variant.getStockQuantity() == null ? 0 : variant.getStockQuantity();
            variant.setStockQuantity(available - cartItem.getQuantity());

            String variantInfo = variant.getColorName() + " / Size " + variant.getSize();
            OrderItem orderItem = OrderItem.builder()
                    .orderId(savedOrder.getId())
                    .productVariantId(variant.getId())
                    .productName(variant.getProduct().getName())
                    .variantInfo(variantInfo)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getPrice())
                    .build();
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);

        if (appliedPromotionCode != null) {
            promotionService.markPromotionUsed(appliedPromotionCode);
        }

        cartService.clearCart(userId);

        // Map to DTO
        return OrderDTO.builder()
                .id(savedOrder.getId())
                .status(savedOrder.getStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .shippingFee(savedOrder.getShippingFee())
                .recipientName(savedOrder.getRecipientName())
                .recipientPhone(savedOrder.getRecipientPhone())
                .shippingAddress(savedOrder.getShippingAddress())
                .paymentMethod(savedOrder.getPaymentMethod())
                .paymentStatus(savedOrder.getPaymentStatus())
                .note(savedOrder.getNote())
                .promotionCode(savedOrder.getPromotionCode())
                .discountAmount(savedOrder.getDiscountAmount())
                .userId(savedOrder.getUserId())
                .createdDate(savedOrder.getCreatedDate())
                .build();
    }

    private void ensurePurchasable(ProductVariant variant, int requestedQuantity) {
        if (!Boolean.TRUE.equals(variant.getActive()) || !Boolean.TRUE.equals(variant.getProduct().getActive())) {
            throw new AppException(ErrorCode.PRODUCT_ALREADY_INACTIVE, variant.getProduct().getName());
        }
        int available = variant.getStockQuantity() == null ? 0 : variant.getStockQuantity();
        if (available <= 0) {
            throw new AppException(ErrorCode.VARIANT_OUT_OF_STOCK, variant.getSku());
        }
        if (requestedQuantity > available) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK, variant.getSku(), available, requestedQuantity);
        }
    }
}
