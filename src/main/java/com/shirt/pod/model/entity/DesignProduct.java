package com.shirt.pod.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User-created design that can be saved and optionally shared.
 * design_json_ref points to MongoDB document; full JSON stored in MongoDB Atlas.
 */
@Entity
@Table(name = "design_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignProduct extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_product_id")
    private BaseProduct baseProduct;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "design_json_ref", nullable = false, length = 64)
    private String designJsonRef;

    @Column(name = "preview_image_url", length = 2048)
    private String previewImageUrl;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;
}
