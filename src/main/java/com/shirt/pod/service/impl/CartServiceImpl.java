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
        ensureVariantCanBePurchased(productVariant, request.getQuantity());

        BigDecimal price = calculatePrice(productVariant);
        
        // Check if item already exists in cart, if so, increase quantity
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(productVariant.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int nextQuantity = existingItem.getQuantity() + request.getQuantity();
            ensureVariantCanBePurchased(productVariant, nextQuantity);
            existingItem.setQuantity(nextQuantity);
            cartItemRepository.save(existingItem);
        } else {
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

        if (request.getSize() != null && !request.getSize().equals(cartItem.getProductVariant().getSize())) {
            ProductVariant currentVariant = cartItem.getProductVariant();
            ProductVariant newVariant = productVariantRepository
                    .findByProductIdAndColorNameAndSizeAndActiveTrue(
                            currentVariant.getProduct().getId(),
                            currentVariant.getColorName(),
                            request.getSize())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy size " + request.getSize() + " cho sản phẩm này"));
            ensureVariantCanBePurchased(newVariant, request.getQuantity());
            cartItem.setProductVariant(newVariant);
            cartItem.setPrice(calculatePrice(newVariant));
        } else {
            ensureVariantCanBePurchased(cartItem.getProductVariant(), request.getQuantity());
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
        BigDecimal basePrice = productVariant.getProduct().getBasePrice();
        BigDecimal priceAdjustment = productVariant.getPriceAdjustment() != null 
                ? productVariant.getPriceAdjustment() 
                : BigDecimal.ZERO;
        return basePrice.add(priceAdjustment);
    }

    private void ensureVariantCanBePurchased(ProductVariant variant, int requestedQuantity) {
        if (!Boolean.TRUE.equals(variant.getActive()) || !Boolean.TRUE.equals(variant.getProduct().getActive())) {
            throw new AppException(ErrorCode.PRODUCT_ALREADY_INACTIVE, variant.getProduct().getName());
        }
        int available = variant.getStockQuantity() == null ? 0 : variant.getStockQuantity();
        if (available <= 0) {
            throw new AppException(ErrorCode.VARIANT_OUT_OF_STOCK, variant.getSku());
        }
        if (requestedQuantity > available) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK, variant.getSku(), available, requestedQuantity);
        }
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

        String imageUrl = variant.getFrontImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = variant.getProduct().getImageUrl();
        }
        String productName = variant.getProduct().getName();

        List<String> availableSizes = productVariantRepository
                .findByProductIdAndActiveTrue(variant.getProduct().getId())
                .stream()
                .filter(v -> v.getColorName().equals(variant.getColorName()))
                .map(ProductVariant::getSize)
                .distinct()
                .sorted((a, b) -> {
                    List<String> order = List.of("XS", "S", "M", "L", "XL", "XXL", "3XL");
                    int ia = order.indexOf(a), ib = order.indexOf(b);
                    return Integer.compare(ia < 0 ? 99 : ia, ib < 0 ? 99 : ib);
                })
                .toList();

        return CartItemDTO.builder()
                .id(item.getId())
                .productVariantId(variant.getId())
                .productName(productName)
                .colorName(variant.getColorName())
                .size(variant.getSize())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .imageUrl(imageUrl)
                .availableSizes(availableSizes)
                .build();
    }
}
