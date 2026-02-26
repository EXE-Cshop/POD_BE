package com.shirt.pod.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class DashboardStatsDTO {
    private Long totalOrders;
    private Long pendingOrders;
    private Long paidOrders;
    private Long processingOrders;
    private Long shippedOrders;
    private Long completedOrders;
    private Long cancelledOrders;

    private BigDecimal totalRevenue;
    private BigDecimal pendingRevenue;
    private BigDecimal paidRevenue;
    private BigDecimal completedRevenue;
}
