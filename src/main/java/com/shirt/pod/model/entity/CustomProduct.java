package com.shirt.pod.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomProduct extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_product_id", nullable = false)
    private BaseProduct baseProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "preview_image_url", length = 2048)
    private String previewImageUrl;

    @Column(name = "front_print_url", length = 2048)
    private String frontPrintUrl;

    @Column(name = "back_print_url", length = 2048)
    private String backPrintUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "design_product_id")
    private DesignProduct designProduct;
}
