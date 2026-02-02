package com.shirt.pod.model.entity;

import com.shirt.pod.model.entity.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntityCreatedOnly {

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "recipient_name")
    private String recipientName;
    @Column(name = "recipient_phone")
    private String recipientPhone;
    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "payment_method")
    private String paymentMethod; // Hoặc Enum

    @Column(name = "payment_status")
    private String paymentStatus;

    private String note;
    private Long userId;
}

// Enum trạng thái đơn
