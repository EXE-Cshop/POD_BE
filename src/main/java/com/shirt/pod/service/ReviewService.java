package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.CreateReviewRequest;
import com.shirt.pod.model.dto.response.ReviewDTO;
import org.springframework.data.domain.Page;

public interface ReviewService {
    Page<ReviewDTO> getReviewsByProduct(Long productId, int page, int size);
    ReviewDTO createReview(Long productId, Long userId, CreateReviewRequest request);
    ReviewDTO updateReview(Long reviewId, Long userId, CreateReviewRequest request);
    void deleteReview(Long reviewId, Long userId);
    Double getAverageRating(Long productId);
    Long getReviewCount(Long productId);
}
