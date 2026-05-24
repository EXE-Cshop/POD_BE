package com.shirt.pod.model.dto.response;

import com.shirt.pod.model.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderDetailDTO {
    private Long id;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String note;
    private String promotionCode;
    private BigDecimal discountAmount;
    private Long userId;
    private Instant createdDate;
    private List<OrderItemDTO> orderItems;
}
