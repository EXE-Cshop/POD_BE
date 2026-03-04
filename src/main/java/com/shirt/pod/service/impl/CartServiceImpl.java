package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.model.dto.request.AddToCartRequest;
import com.shirt.pod.model.dto.request.UpdateCartItemRequest;
import com.shirt.pod.model.dto.response.CartDTO;
import com.shirt.pod.model.dto.response.CartItemDTO;
import com.shirt.pod.model.entity.Cart;
import com.shirt.pod.model.entity.CartItem;
import com.shirt.pod.model.entity.ProductVariant;
import com.shirt.pod.model.entity.User;
import com.shirt.pod.repository.CartItemRepository;
import com.shirt.pod.repository.CartRepository;
import com.shirt.pod.repository.ProductVariantRepository;
import com.shirt.pod.repository.UserRepository;
import com.shirt.pod.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartDTO addToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        
        ProductVariant productVariant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));

        // Check if product already exists in cart
        CartItem existingItem = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId())
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            // Create new cart item
            BigDecimal price = calculatePrice(productVariant);
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(productVariant)
                    .quantity(request.getQuantity())
                    .price(price)
                    .build();
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        cartRepository.save(cart);
        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO removeCartItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);

        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO getCurrentCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Cart newCart = Cart.builder()
                    .user(user)
                    .build();
            return cartRepository.save(newCart);
        });
    }

    private BigDecimal calculatePrice(ProductVariant productVariant) {
        BigDecimal basePrice = productVariant.getBaseProduct().getBasePrice();
        BigDecimal priceAdjustment = productVariant.getPriceAdjustment() != null 
                ? productVariant.getPriceAdjustment() 
                : BigDecimal.ZERO;
        return basePrice.add(priceAdjustment);
    }

    private CartDTO mapToCartDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(this::mapToCartItemDTO)
                .collect(Collectors.toList());

        BigDecimal totalAmount = itemDTOs.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = itemDTOs.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();

        return CartDTO.builder()
                .id(cart.getId())
                .items(itemDTOs)
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .build();
    }

    private CartItemDTO mapToCartItemDTO(CartItem item) {
        ProductVariant variant = item.getProductVariant();
        return CartItemDTO.builder()
                .id(item.getId())
                .productVariantId(variant.getId())
                .productName(variant.getBaseProduct().getName())
                .colorName(variant.getColorName())
                .size(variant.getSize())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}
