package com.shirt.pod.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "base_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseProduct extends BaseEntity {

    private String name; // Ví dụ: Áo thun Cotton 4 chiều

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_price")
    private BigDecimal basePrice; // Giá gốc chưa tính công in và biến thể

    private String material; // Chất liệu: 100% Cotton, Canvas...

    private String printTechnology; // Công nghệ in mặc định: DTG, Decal, In chuyển nhiệt

    @Column(name = "image_url")
    private String imageUrl; // URL ảnh sản phẩm mặc định

    private Boolean active;

    @OneToMany(mappedBy = "baseProduct", cascade = CascadeType.ALL)
    private List<PrintArea> printAreas; // Một áo có nhiều vùng in (trước, sau)

    @OneToMany(mappedBy = "baseProduct", cascade = CascadeType.ALL)
    private List<ProductVariant> variants; // Một áo có nhiều màu, size

}