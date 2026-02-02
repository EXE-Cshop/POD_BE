package com.shirt.pod.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private String colorName; // Màu Đen
    private String colorHex; // #000000
    private String size; // S, M, L, XL
    @Column(unique = true)
    private String sku; // Mã quản lý kho (ví dụ: TEE-BLK-L)
    private Integer stockQuantity;
    // QUAN TRỌNG: Ảnh mockup phôi màu này (Chưa có hình in)
    // Dùng làm background cho khách kéo thả thiết kế
    private String frontImageUrl;
    private String backImageUrl;
    private BigDecimal priceAdjustment; // Giá cộng thêm (ví dụ size XXL đắt hơn 10k)
    private Boolean active;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_product_id")
    private BaseProduct baseProduct;
}