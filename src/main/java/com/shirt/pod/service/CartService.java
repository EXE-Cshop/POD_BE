package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.AddToCartRequest;
import com.shirt.pod.model.dto.request.UpdateCartItemRequest;
import com.shirt.pod.model.dto.response.CartDTO;

public interface CartService {
    CartDTO addToCart(Long userId, AddToCartRequest request);
    CartDTO updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request);
    CartDTO removeCartItem(Long userId, Long itemId);
    CartDTO getCurrentCart(Long userId);
    void clearCart(Long userId);
}
