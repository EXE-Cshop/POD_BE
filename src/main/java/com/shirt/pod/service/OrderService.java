package com.shirt.pod.service;

import com.shirt.pod.model.dto.response.DashboardStatsDTO;
import com.shirt.pod.model.dto.response.OrderDTO;
import com.shirt.pod.model.dto.response.OrderDetailDTO;
import com.shirt.pod.model.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;

public interface OrderService {

    Page<OrderDTO> getOrders(OrderStatus status, int page, int size, String sortBy, String order);

    DashboardStatsDTO getDashboardStats();

    OrderDetailDTO getOrderDetail(Long orderId);

    void processPendingOrders();
}
