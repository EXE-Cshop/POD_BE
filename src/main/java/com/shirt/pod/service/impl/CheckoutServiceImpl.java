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
import com.shirt.pod.model.entity.User;
import com.shirt.pod.model.entity.enums.OrderStatus;
import com.shirt.pod.repository.CartRepository;
import com.shirt.pod.repository.OrderRepository;
import com.shirt.pod.repository.UserRepository;
import com.shirt.pod.service.CartService;
import com.shirt.pod.service.CheckoutService;
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
    private final UserRepository userRepository;
    private final CartService cartService;

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

        // Calculate total amount
        BigDecimal totalAmount = cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .shippingFee(BigDecimal.ZERO)
                .recipientName(user.getFullName())
                .recipientPhone(user.getPhoneNumber())
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod().name())
                .paymentStatus("UNPAID")
                .note(request.getNote())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Create order items from cart items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getPrice())
                    .productionStatus("WAITING")
                    .build();
            orderItems.add(orderItem);
        }

        // Clear cart
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
                .userId(savedOrder.getUserId())
                .createdDate(savedOrder.getCreatedDate())
                .build();
    }
}
