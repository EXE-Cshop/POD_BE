package com.shirt.pod.service.impl;

import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.model.dto.request.CreateReviewRequest;
import com.shirt.pod.model.dto.response.ReviewDTO;
import com.shirt.pod.model.entity.Product;
import com.shirt.pod.model.entity.Review;
import com.shirt.pod.model.entity.User;
import com.shirt.pod.repository.ProductRepository;
import com.shirt.pod.repository.ReviewRepository;
import com.shirt.pod.repository.UserRepository;
import com.shirt.pod.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Page<ReviewDTO> getReviewsByProduct(Long productId, int page, int size) {
        return reviewRepository.findByProductId(productId,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate")))
                .map(this::toDTO);
    }

    @Override
    @Transactional
    public ReviewDTO createReview(Long productId, Long userId, CreateReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .verified(false)
                .build();
        return toDTO(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(Long reviewId, Long userId, CreateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only edit your own reviews");
        }
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return toDTO(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }
        reviewRepository.delete(review);
    }

    @Override
    public Double getAverageRating(Long productId) {
        return reviewRepository.getAverageRatingByProductId(productId).orElse(0.0);
    }

    @Override
    public Long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    private ReviewDTO toDTO(Review r) {
        return ReviewDTO.builder()
                .id(r.getId())
                .productId(r.getProduct().getId())
                .userId(r.getUser().getId())
                .userName(r.getUser().getFullName())
                .userAvatar(r.getUser().getAvatarUrl())
                .rating(r.getRating())
                .comment(r.getComment())
                .verified(r.getVerified())
                .createdDate(r.getCreatedDate())
                .build();
    }
}
