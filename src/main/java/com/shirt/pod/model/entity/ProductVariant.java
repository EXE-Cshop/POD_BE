package com.shirt.pod.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends BaseEntityCreatedOnly {
    private String colorName;
    private String colorHex;
    private String size;
    @Column(unique = true)
    private String sku;
    private Integer stockQuantity;
    private String frontImageUrl;
    private String backImageUrl;
    private BigDecimal priceAdjustment;
    @Builder.Default
    private Boolean active = true;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_product_id")
    private Product product;
}