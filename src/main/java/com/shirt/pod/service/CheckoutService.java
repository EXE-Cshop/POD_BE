package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.CheckoutRequest;
import com.shirt.pod.model.dto.response.OrderDTO;

public interface CheckoutService {
    OrderDTO checkout(Long userId, CheckoutRequest request);
}
