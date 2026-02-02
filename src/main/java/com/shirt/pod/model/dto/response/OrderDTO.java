package com.shirt.pod.model.dto.response;

import com.shirt.pod.model.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class OrderDTO {
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
    private Long userId;
    private Instant createdDate;
}
