package com.shirt.pod.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntityCreatedOnly {

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_variant_id")
    private Long productVariantId;

    @Column(name = "product_name")
    private String productName; // Snapshot tên sản phẩm tại thời điểm đặt hàng

    @Column(name = "variant_info")
    private String variantInfo; // Snapshot: "Đen / Size L"

    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;
}
