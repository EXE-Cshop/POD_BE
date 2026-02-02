package com.shirt.pod.model.entity;

import com.shirt.pod.model.entity.enums.PrintAreaName;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "print_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrintArea extends BaseEntityCreatedOnly {
    @Enumerated(EnumType.STRING) // Lưu vào DB dưới dạng chữ (ví dụ: "FRONT_CENTER")
    private PrintAreaName name; // Mặt trước, Mặt sau, Ngực trái...
    // Kích thước thực tế để gửi nhà in
    private BigDecimal widthMm;
    private BigDecimal heightMm;
    // Tọa độ vùng in so với ảnh Mockup (Lưu dạng % để hiển thị Responsive)
    // Ví dụ: top 20% có nghĩa là vùng in bắt đầu từ vị trí 1/5 chiều cao ảnh tính từ trên xuống
    private Double topOffsetPercent;
    private Double leftOffsetPercent;
    private Double widthPercent; // Độ rộng vùng in so với ảnh mockup (%)
    private Double heightPercent; // Độ cao vùng in so với ảnh mockup (%)
    private String maskImageUrl; // Ảnh overlay có đục lỗ vùng in để tạo hiệu ứng thị giác
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_product_id")
    private BaseProduct baseProduct;
}