package com.shirt.pod.controller;

import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.DashboardStatsDTO;
import com.shirt.pod.model.dto.response.OrderDetailDTO;
import com.shirt.pod.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import com.shirt.pod.security.SecurityConstants;

import java.time.Instant;


@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Dashboard", description = "Admin Dashboard APIs - Statistics and Order Details")
public class AdminDashboardController {

    private final OrderService orderService;

    @GetMapping("/stats")
    @Operation(summary = "Get Dashboard Statistics", description = "Lấy thống kê doanh thu và số lượng đơn hàng theo trạng thái")
    @PreAuthorize("hasAuthority('" + SecurityConstants.DASHBOARD_VIEW + "')")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        log.info("GET /api/v1/admin/dashboard/stats - Fetching dashboard statistics");

        DashboardStatsDTO stats = orderService.getDashboardStats();

        ApiResponse<DashboardStatsDTO> response = ApiResponse.<DashboardStatsDTO>builder()
                .data(stats)
                .code(HttpStatus.OK.value())
                .message("Get dashboard statistics successfully")
                .timestamp(Instant.now().toEpochMilli())
                .build();

        log.info("Successfully fetched dashboard statistics");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get Order Detail", description = "Lấy chi tiết đơn hàng cùng với danh sách order items (Join bảng Order & OrderItem)")
    @PreAuthorize("hasAuthority('" + SecurityConstants.ORDER_VIEW + "')")
    public ResponseEntity<ApiResponse<OrderDetailDTO>> getOrderDetail(@PathVariable Long orderId) {
        log.info("GET /api/v1/admin/dashboard/orders/{} - Fetching order detail", orderId);

        OrderDetailDTO orderDetail = orderService.getOrderDetail(orderId);

        ApiResponse<OrderDetailDTO> response = ApiResponse.<OrderDetailDTO>builder()
                .data(orderDetail)
                .code(HttpStatus.OK.value())
                .message("Get order detail successfully")
                .timestamp(Instant.now().toEpochMilli())
                .build();

        log.info("Successfully fetched order detail for orderId: {}", orderId);
        return ResponseEntity.ok(response);
    }
}
