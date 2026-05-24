package com.shirt.pod.service.impl;

import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.model.dto.response.WishlistItemDTO;
import com.shirt.pod.model.entity.Product;
import com.shirt.pod.model.entity.User;
import com.shirt.pod.model.entity.WishlistItem;
import com.shirt.pod.repository.ProductRepository;
import com.shirt.pod.repository.UserRepository;
import com.shirt.pod.repository.WishlistItemRepository;
import com.shirt.pod.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<WishlistItemDTO> getWishlist(Long userId) {
        return wishlistItemRepository.findByUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WishlistItemDTO addToWishlist(Long userId, Long productId) {
        if (wishlistItemRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Product already in wishlist");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        WishlistItem item = WishlistItem.builder()
                .user(user)
                .product(product)
                .build();
        return toDTO(wishlistItemRepository.save(item));
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistItemRepository.existsByUserIdAndProductId(userId, productId);
    }

    private WishlistItemDTO toDTO(WishlistItem item) {
        Product p = item.getProduct();
        return WishlistItemDTO.builder()
                .id(item.getId())
                .productId(p.getId())
                .productName(p.getName())
                .productSlug(p.getSlug())
                .productImageUrl(p.getImageUrl())
                .productPrice(p.getBasePrice() != null ? p.getBasePrice().toString() : "0")
                .addedDate(item.getCreatedDate())
                .build();
    }
}
