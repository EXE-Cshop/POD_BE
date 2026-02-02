package com.shirt.pod.controller;

import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.OrderDTO;
import com.shirt.pod.model.entity.enums.OrderStatus;
import com.shirt.pod.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get list of orders", description = "Retrieve a paginated list of orders, optionally filtered by status")
    @GetMapping
    public ApiResponse<Page<OrderDTO>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String order) {



        return ApiResponse.<Page<OrderDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Orders fetched successfully")
                .data(orderService.getOrders(status, page, size, sortBy, order))
                .build();
    }
}
