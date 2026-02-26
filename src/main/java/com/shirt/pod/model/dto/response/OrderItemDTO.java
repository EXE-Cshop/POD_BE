package com.shirt.pod.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String printFileUrl;
    private String productionStatus;
    private Instant createdDate;
}
