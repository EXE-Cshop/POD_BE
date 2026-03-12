package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.AddToCartRequest;
import com.shirt.pod.model.dto.request.UpdateCartItemRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.CartDTO;
import com.shirt.pod.security.CustomUserDetails;
import com.shirt.pod.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart management APIs")
@PreAuthorize("isAuthenticated()")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    @Operation(summary = "Add product to cart")
    public ApiResponse<CartDTO> addToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request) {
        CartDTO cartDTO = cartService.addToCart(userDetails.getId(), request);
        
        return ApiResponse.<CartDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Product added to cart successfully")
                .data(cartDTO)
                .build();
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ApiResponse<CartDTO> updateCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartDTO cartDTO = cartService.updateCartItem(userDetails.getId(), itemId, request);
        
        return ApiResponse.<CartDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Cart item updated successfully")
                .data(cartDTO)
                .build();
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ApiResponse<CartDTO> removeCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId) {
        CartDTO cartDTO = cartService.removeCartItem(userDetails.getId(), itemId);
        
        return ApiResponse.<CartDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Cart item removed successfully")
                .data(cartDTO)
                .build();
    }

    @GetMapping
    @Operation(summary = "Get current cart")
    public ApiResponse<CartDTO> getCurrentCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartDTO cartDTO = cartService.getCurrentCart(userDetails.getId());
        
        return ApiResponse.<CartDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Cart retrieved successfully")
                .data(cartDTO)
                .build();
    }
}
