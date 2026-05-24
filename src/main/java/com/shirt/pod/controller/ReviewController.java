package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.CreateReviewRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.ReviewDTO;
import com.shirt.pod.security.CustomUserDetails;
import com.shirt.pod.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Review", description = "Review and rating management APIs")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}/reviews")
    @Operation(summary = "Get reviews for a product")
    public ApiResponse<Page<ReviewDTO>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewDTO> reviews = reviewService.getReviewsByProduct(productId, page, size);
        return ApiResponse.<Page<ReviewDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Reviews retrieved successfully")
                .data(reviews)
                .build();
    }

    @PostMapping("/products/{productId}/reviews")
    @Operation(summary = "Create review for a product")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ReviewDTO> createReview(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewDTO review = reviewService.createReview(productId, userDetails.getId(), request);
        return ApiResponse.<ReviewDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Review created successfully")
                .data(review)
                .build();
    }

    @PutMapping("/reviews/{reviewId}")
    @Operation(summary = "Update an existing review")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewDTO review = reviewService.updateReview(reviewId, userDetails.getId(), request);
        return ApiResponse.<ReviewDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Review updated successfully")
                .data(review)
                .build();
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "Delete a review")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getId());
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Review deleted successfully")
                .build();
    }

    @GetMapping("/products/{productId}/reviews/average")
    @Operation(summary = "Get average rating and count for a product")
    public ApiResponse<Double> getAverageRating(@PathVariable Long productId) {
        Double average = reviewService.getAverageRating(productId);
        return ApiResponse.<Double>builder()
                .code(HttpStatus.OK.value())
                .message("Average rating retrieved successfully")
                .data(average)
                .build();
    }
}
