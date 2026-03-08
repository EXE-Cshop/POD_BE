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
import com.shirt.pod.model.entity.CustomProduct;
import com.shirt.pod.model.entity.ProductVariant;
import com.shirt.pod.model.entity.User;
import com.shirt.pod.repository.CartItemRepository;
import com.shirt.pod.repository.CartRepository;
import com.shirt.pod.repository.CustomProductRepository;
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
    private final CustomProductRepository customProductRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartDTO addToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);

        ProductVariant productVariant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));

        boolean hasDesign = request.getFrontPrintUrl() != null || request.getBackPrintUrl() != null;
        CustomProduct customProduct = null;

        if (hasDesign) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            String name = request.getCustomName() != null
                    ? request.getCustomName()
                    : productVariant.getBaseProduct().getName() + " - Custom Design";
            String previewUrl = request.getFrontPrintUrl() != null
                    ? request.getFrontPrintUrl()
                    : request.getBackPrintUrl();

            customProduct = CustomProduct.builder()
                    .user(user)
                    .baseProduct(productVariant.getBaseProduct())
                    .productVariant(productVariant)
                    .name(name)
                    .previewImageUrl(previewUrl)
                    .frontPrintUrl(request.getFrontPrintUrl())
                    .backPrintUrl(request.getBackPrintUrl())
                    .build();
            customProduct = customProductRepository.save(customProduct);
        }

        BigDecimal price = calculatePrice(productVariant);
        CartItem newItem = CartItem.builder()
                .cart(cart)
                .productVariant(productVariant)
                .quantity(request.getQuantity())
                .price(price)
                .customProduct(customProduct)
                .build();
        cart.addItem(newItem);
        cartItemRepository.save(newItem);

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

        if (request.getSize() != null && !request.getSize().equals(cartItem.getProductVariant().getSize())) {
            ProductVariant currentVariant = cartItem.getProductVariant();
            ProductVariant newVariant = productVariantRepository
                    .findByBaseProductIdAndColorNameAndSizeAndActiveTrue(
                            currentVariant.getBaseProduct().getId(),
                            currentVariant.getColorName(),
                            request.getSize())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy size " + request.getSize() + " cho sản phẩm này"));
            cartItem.setProductVariant(newVariant);
            cartItem.setPrice(calculatePrice(newVariant));
            if (cartItem.getCustomProduct() != null) {
                cartItem.getCustomProduct().setProductVariant(newVariant);
                customProductRepository.save(cartItem.getCustomProduct());
            }
        }

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
        CustomProduct cp = item.getCustomProduct();
        boolean isCustom = cp != null;

        String imageUrl = variant.getFrontImageUrl();
        String productName = variant.getBaseProduct().getName();
        if (isCustom) {
            if (cp.getPreviewImageUrl() != null) imageUrl = cp.getPreviewImageUrl();
            productName = cp.getName();
        }

        List<String> availableSizes = productVariantRepository
                .findByBaseProductIdAndActiveTrue(variant.getBaseProduct().getId())
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
                .customProductId(isCustom ? cp.getId() : null)
                .isCustomDesign(isCustom)
                .availableSizes(availableSizes)
                .build();
    }
}
