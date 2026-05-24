package com.shirt.pod.service;

import com.shirt.pod.model.dto.response.WishlistItemDTO;

import java.util.List;

public interface WishlistService {
    List<WishlistItemDTO> getWishlist(Long userId);
    WishlistItemDTO addToWishlist(Long userId, Long productId);
    void removeFromWishlist(Long userId, Long productId);
    boolean isInWishlist(Long userId, Long productId);
}
