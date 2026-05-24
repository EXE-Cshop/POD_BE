package com.shirt.pod.controller;

import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.WishlistItemDTO;
import com.shirt.pod.security.CustomUserDetails;
import com.shirt.pod.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Wishlist management APIs")
@PreAuthorize("isAuthenticated()")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get all items in the user's wishlist")
    public ApiResponse<List<WishlistItemDTO>> getWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<WishlistItemDTO> wishlist = wishlistService.getWishlist(userDetails.getId());
        return ApiResponse.<List<WishlistItemDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Wishlist retrieved successfully")
                .data(wishlist)
                .build();
    }

    @PostMapping("/{productId}")
    @Operation(summary = "Add a product to the wishlist")
    public ApiResponse<WishlistItemDTO> addToWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId) {
        WishlistItemDTO item = wishlistService.addToWishlist(userDetails.getId(), productId);
        return ApiResponse.<WishlistItemDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Product added to wishlist successfully")
                .data(item)
                .build();
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove a product from the wishlist")
    public ApiResponse<Void> removeFromWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(userDetails.getId(), productId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Product removed from wishlist successfully")
                .build();
    }

    @GetMapping("/{productId}/status")
    @Operation(summary = "Check if a product is in the user's wishlist")
    public ApiResponse<Boolean> isInWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId) {
        boolean exists = wishlistService.isInWishlist(userDetails.getId(), productId);
        return ApiResponse.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Checked wishlist status successfully")
                .data(exists)
                .build();
    }
}
